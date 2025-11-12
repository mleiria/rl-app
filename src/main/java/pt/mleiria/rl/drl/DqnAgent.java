package pt.mleiria.rl.drl;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.dataset.Batch;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.training.tracker.Tracker;
import ai.djl.translate.Batchifier;

import java.util.*;

public class DqnAgent {

    private final int stateSize;
    private final int actionSize;

    // Hyperparameters
    private final Deque<Experience> memory = new LinkedList<>();
    private final int memoryCapacity = 2000;
    private final double gamma = 0.95;    // Discount factor
    private double epsilon = 1.0;   // Exploration rate
    private final double epsilonMin = 0.01;
    private final double epsilonDecay = 0.995;
    private final float learningRate = 0.001f;

    private final Model model;
    private final Trainer trainer;

    public DqnAgent(int stateSize, int actionSize) {
        this.stateSize = stateSize;
        this.actionSize = actionSize;

        // The Deep Neural Network model
        this.model = buildModel();

        // The trainer is responsible for updating the model's parameters
        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.l2Loss()) // MSE Loss
                .optOptimizer(
                        Adam.builder().optLearningRateTracker(Tracker.fixed(learningRate)).build());
        this.trainer = model.newTrainer(config);
    }

    private Model buildModel() {
        Model model = Model.newInstance("dqn");
        SequentialBlock block = new SequentialBlock();
        block.add(Linear.builder().setUnits(24).build());
        block.add(ai.djl.nn.Activation::relu);
        block.add(Linear.builder().setUnits(24).build());
        block.add(ai.djl.nn.Activation::relu);
        block.add(Linear.builder().setUnits(actionSize).build());
        model.setBlock(block);
        return model;
    }

    // A simple class to hold an experience tuple
    private static class Experience {
        float[] state;
        int action;
        float reward;
        float[] nextState;
        boolean done;

        Experience(float[] state, int action, float reward, float[] nextState, boolean done) {
            this.state = state;
            this.action = action;
            this.reward = reward;
            this.nextState = nextState;
            this.done = done;
        }
    }

    /**
     * Stores an experience in the replay memory.
     */
    public void remember(float[] state, int action, float reward, float[] nextState, boolean done) {
        if (memory.size() >= memoryCapacity) {
            memory.removeFirst();
        }
        memory.add(new Experience(state, action, reward, nextState, done));
    }

    /**
     * Chooses an action using the Epsilon-Greedy policy.
     */
    public int act(NDManager manager, float[] state) {
        if (new Random().nextDouble() <= epsilon) {
            return new Random().nextInt(actionSize); // Explore
        }

        // Exploit: Use the model to predict the best action
        NDArray stateArray = manager.create(state).reshape(1, stateSize);
        NDList result = trainer.forward(new NDList(stateArray));
        NDArray qValues = result.singletonOrThrow();
        return (int) qValues.argMax().getLong();
    }

    /**
     * Trains the network using a random batch from the replay memory.
     */
    public void replay(int batchSize) {
        if (memory.size() < batchSize) {
            return; // Don't train if memory is not full enough
        }

        List<Experience> minibatch = new LinkedList<>(memory);
        Collections.shuffle(minibatch);
        minibatch = minibatch.subList(0, batchSize);

        try (NDManager manager = NDManager.newBaseManager()) {
            // Convert the batch of experiences to NDArrays
            float[][] states = new float[batchSize][stateSize];
            float[][] nextStates = new float[batchSize][stateSize];
            int[] actions = new int[batchSize];
            float[] rewards = new float[batchSize];
            boolean[] dones = new boolean[batchSize];

            for (int i = 0; i < batchSize; i++) {
                Experience e = minibatch.get(i);
                states[i] = e.state;
                nextStates[i] = e.nextState;
                actions[i] = e.action;
                rewards[i] = e.reward;
                dones[i] = e.done;
            }

            NDArray statesArray = manager.create(states);
            statesArray.reshape(new Shape(batchSize, stateSize));
            NDArray nextStatesArray = manager.create(nextStates);
            nextStatesArray.reshape(new Shape(batchSize, stateSize));

            // The Bellman Equation: target = reward + gamma * max(Q(next_state))
            NDArray futureRewards = trainer.forward(new NDList(nextStatesArray)).singletonOrThrow();
            NDArray maxFutureRewards = futureRewards.max(new int[]{1});

            // Calculate target Q-values
            NDArray targetQValues = trainer.forward(new NDList(statesArray)).singletonOrThrow().duplicate();
            for (int i = 0; i < batchSize; i++) {
                float target = rewards[i];
                if (!dones[i]) {
                    target += gamma * maxFutureRewards.getFloat(i);
                }
                targetQValues.set(new ai.djl.ndarray.index.NDIndex(i, actions[i]), target);
            }

            // Train the model
            NDList data = new NDList(statesArray);
            NDList labels = new NDList(targetQValues);
            Batch batch = new Batch(manager, data, labels, batchSize, Batchifier.STACK, Batchifier.STACK, 0L, 0L);
            EasyTrain.trainBatch(trainer, batch);
            batch.close();
            trainer.step();
        }

        // Decay epsilon to reduce exploration over time
        if (epsilon > epsilonMin) {
            epsilon *= epsilonDecay;
        }
    }

    // --- Main Training Loop ---
    public static void main(String[] args) throws Exception {
        // Environment parameters
        final int STATE_SIZE = 1;
        final int ACTION_SIZE = 2; // 0: move left, 1: move right
        final int EPISODES = 500;
        final int BATCH_SIZE = 32;

        DqnAgent agent = new DqnAgent(STATE_SIZE, ACTION_SIZE);
        agent.trainer.initialize(new Shape(1, STATE_SIZE)); // Initialize model parameters

        // The simple 1D environment
        int lineSize = 5;
        int goalPosition = 4;

        for (int e = 0; e < EPISODES; e++) {
            // Reset the environment for a new episode
            float[] state = {0f};

            for (int time = 0; time < 50; time++) {
                try (NDManager manager = NDManager.newBaseManager()) {
                    // Agent chooses an action
                    int action = agent.act(manager, state);

                    // Environment reacts to the action
                    float[] nextState = {state[0]};
                    if (action == 0) { // Move left
                        nextState[0] = Math.max(0, state[0] - 1);
                    } else if (action == 1) { // Move right
                        nextState[0] = Math.min(lineSize - 1, state[0] + 1);
                    }

                    // Determine the reward
                    boolean done = nextState[0] == goalPosition;
                    float reward = done ? 10f : -1f;

                    // Agent stores this experience
                    agent.remember(state, action, reward, nextState, done);

                    // Update the state
                    state = nextState;

                    if (done) {
                        System.out.printf("Episode: %d/%d, Score: %d, Epsilon: %.2f%n",
                                e + 1, EPISODES, time + 1, agent.epsilon);
                        break;
                    }
                }
            }
            // Train the agent with experience replay
            agent.replay(BATCH_SIZE);
        }
    }
}
