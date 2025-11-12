package pt.mleiria.rl.drl;

import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.GradientCollector;
import ai.djl.training.Trainer;
import ai.djl.training.dataset.ArrayDataset;
import ai.djl.training.dataset.Batch;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Optimizer;
import ai.djl.training.tracker.Tracker;
import ai.djl.translate.NoopTranslator;

import java.util.Random;

public class SurvivalClassifierDJL {

    // --- Step 1: Generate the Fake Dataset ---
    static class Data {
        NDArray features; // shape: (N, 5)
        NDArray labels;   // shape: (N, 1)  (1=dead, 0=alive)
        Data(NDArray f, NDArray l) { this.features = f; this.labels = l; }
    }

    static Data createFakeSurvivalData(NDManager manager, int numSamples, long seed) {
        Random rng = new Random(seed);

        float[] x = new float[numSamples];
        float[] y = new float[numSamples];
        float[] hunger = new float[numSamples];
        float[] stress = new float[numSamples];
        float[] density = new float[numSamples];
        float[] labels = new float[numSamples];

        for (int i = 0; i < numSamples; i++) {
            x[i] = rng.nextFloat() * 100f;
            y[i] = rng.nextFloat() * 100f;
            hunger[i] = rng.nextFloat();
            stress[i] = rng.nextFloat();
            density[i] = rng.nextFloat() * 50f;

            boolean condition1 = (stress[i] > 0.8f) && (hunger[i] > 0.85f);
            boolean condition2 = (density[i] > 45f);
            float label = (condition1 || condition2) ? 1f : 0f;

            // 5% label flip noise
            boolean flip = rng.nextFloat() < 0.05f;
            labels[i] = Math.abs(label - (flip ? 1f : 0f));
        }

        // Stack into features: [x, y, hunger, stress, density]
        float[] feat = new float[numSamples * 5];
        for (int i = 0; i < numSamples; i++) {
            int base = i * 5;
            feat[base]     = x[i];
            feat[base + 1] = y[i];
            feat[base + 2] = hunger[i];
            feat[base + 3] = stress[i];
            feat[base + 4] = density[i];
        }

        NDArray features = manager.create(feat, new Shape(numSamples, 5));
        NDArray labelArr = manager.create(labels, new Shape(numSamples, 1));

        long alive = labelArr.eq(0).sum().toLongArray()[0];
        long dead  = labelArr.eq(1).sum().toLongArray()[0];
        System.out.println("Generated " + numSamples + " samples.");
        System.out.println("Number of 'alive' mice (0): " + alive);
        System.out.println("Number of 'dead' mice (1): " + dead + "\n");

        return new Data(features, labelArr);
    }

    // --- Step 3: Define the Neural Network ---
    static Block buildNetwork() {
        return new SequentialBlock()
                .add(Linear.builder().setUnits(32).build())
                .add(Activation::relu)
                .add(Linear.builder().setUnits(16).build())
                .add(Activation::relu)
                .add(Linear.builder().setUnits(1).build()); // output: logit
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Using engine: " + Engine.getInstance().getEngineName());

        try (NDManager manager = NDManager.newBaseManager();
             Model model = Model.newInstance("survival-classifier")) {

            // --- Step 1 & 2: Data ---
            int numSamples = 10000;
            int batchSize = 32;
            Data data = createFakeSurvivalData(manager, numSamples, 1234L);

            ArrayDataset dataset = new ArrayDataset.Builder()
                    .setData(data.features)       // X
                    .optLabels(data.labels)       // y (shape Nx1)
                    .setSampling(batchSize, true) // shuffle = true
                    .build();

            // --- Step 3: Model ---
            model.setBlock(buildNetwork());

            // --- Step 4: Train ---
            int epochs = 200;
            float learningRate = 0.005f;

            DefaultTrainingConfig config = new DefaultTrainingConfig(
                    // BCEWithLogitsLoss equivalent (sigmoid + BCE, numerically stable)
                    Loss.sigmoidBinaryCrossEntropyLoss())
                    .optOptimizer(Optimizer.adam()
                            .optLearningRateTracker(Tracker.fixed(learningRate))
                            .build())
                    .addTrainingListeners(TrainingListener.Defaults.logging());

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.initialize(new Shape(1, 5)); // input shape

                System.out.println("Starting training...");
                for (int epoch = 1; epoch <= epochs; epoch++) {
                    double epochLossSum = 0.0;
                    int numBatches = 0;

                    for (Batch batch : trainer.iterateDataset(dataset)) {
                        float batchLoss;

                        // Everything that must track gradients stays inside this scope
                        try (GradientCollector gc = Engine.getInstance().newGradientCollector()) {
                            // Forward
                            NDList preds = trainer.forward(batch.getData());     // uses trainer's ParameterStore
                            NDArray yPred = preds.singletonOrThrow();            // (B, 1)

                            // Labels
                            NDArray yTrue = batch.getLabels().head();            // (B, 1)

                            // Loss (BCE-with-logits)
                            NDArray lossTensor = trainer.getLoss()
                                    .evaluate(new NDList(yTrue), new NDList(yPred));

                            // Backprop through the mean loss (scalar)
                            NDArray meanLoss = lossTensor.mean();
                            gc.backward(meanLoss);

                            batchLoss = meanLoss.getFloat();
                        }

                        // Optimizer step after gradients are collected
                        trainer.step();

                        epochLossSum += batchLoss;
                        numBatches++;

                        batch.close();
                    }

                    double epochLoss = epochLossSum / Math.max(1, numBatches);
                    System.out.printf("Epoch [%d/%d], Loss: %.4f%n", epoch, epochs, epochLoss);
                }
                System.out.println("Training finished.\n");

                // --- Step 5: Prediction ---
                try (Predictor<NDList, NDList> predictor = model.newPredictor(new NoopTranslator())) {
                    NDArray mouseHealthy = manager.create(new float[]{
                            50f, 50f, 0.1f, 0.1f, 5f
                    }, new Shape(1, 5));
                    NDArray mouseStressed = manager.create(new float[]{
                            25f, 30f, 0.9f, 0.9f, 15f
                    }, new Shape(1, 5));
                    NDList outHealthy  = predictor.predict(new NDList(mouseHealthy));
                    NDList outStressed = predictor.predict(new NDList(mouseStressed));

                    NDArray healthyLogit  = outHealthy.singletonOrThrow();
                    NDArray stressedLogit = outStressed.singletonOrThrow();

                    NDArray healthyProb  = Activation.sigmoid(healthyLogit);   // P(dead)
                    NDArray stressedProb = Activation.sigmoid(stressedLogit);  // P(dead)

                    float hp = healthyProb.toFloatArray()[0];
                    float sp = stressedProb.toFloatArray()[0];

                    String healthyPred  = (hp < 0.5f) ? "Alive" : "Dead";
                    String stressedPred = (sp < 0.5f) ? "Alive" : "Dead";

                    System.out.println("--- Making Predictions on New Data ---");
                    System.out.printf("Healthy Mouse -> Survival Probability: %.4f, Prediction: %s%n", hp, healthyPred);
                    System.out.printf("Stressed Mouse -> Survival Probability: %.4f, Prediction: %s%n", sp, stressedPred);
                }
            }
        }
    }

    // Simple average helper for loss retrieval if you decide to wire one.
    // (You can omit this if you don't end up using trainer.getLoss().getAccumulator)
    static class Average {
        static final String LOSS = "Loss";
    }
}

