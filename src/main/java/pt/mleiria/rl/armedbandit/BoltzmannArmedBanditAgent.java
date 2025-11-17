package pt.mleiria.rl.armedbandit;

import java.util.Random;

/**
 * An agent that follows the Boltzmann (Softmax) exploration strategy.
 */
class BoltzmannArmedBanditAgent implements ArmedBanditAgent {
    private final double temperature;      // τ (controls exploration)
    private final int numArms;
    private final int[] armPullCounts;
    private final double[] estimatedValues;
    private final Random random;

    public BoltzmannArmedBanditAgent(int numArms, double temperature) {
        this.numArms = numArms;
        this.temperature = temperature;
        this.armPullCounts = new int[numArms];
        this.estimatedValues = new double[numArms];
        this.random = new Random();
    }

    @Override
    public int chooseArm() {
        // Compute softmax probabilities
        double[] probabilities = new double[numArms];
        double sumExp = 0.0;

        // To improve numerical stability, subtract the max Q value
        double maxQ = Double.NEGATIVE_INFINITY;
        for (double q : estimatedValues) {
            if (q > maxQ) {
                maxQ = q;
            }
        }

        for (int i = 0; i < numArms; i++) {
            probabilities[i] = Math.exp((estimatedValues[i] - maxQ) / temperature);
            sumExp += probabilities[i];
        }

        // Normalize probabilities
        for (int i = 0; i < numArms; i++) {
            probabilities[i] /= sumExp;
        }

        // Select arm according to probabilities
        double r = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < numArms; i++) {
            cumulative += probabilities[i];
            if (r < cumulative) {
                return i;
            }
        }

        // Fallback (numerical safety)
        return numArms - 1;
    }

    @Override
    public void update(int armIndex, int reward) {
        this.armPullCounts[armIndex]++;
        int n = this.armPullCounts[armIndex];
        double oldValue = this.estimatedValues[armIndex];
        this.estimatedValues[armIndex] = oldValue + (1.0 / n) * (reward - oldValue);
    }

    @Override
    public double[] getEstimatedValues() {
        return this.estimatedValues;
    }

    @Override
    public int[] getArmPullCounts() {
        return this.armPullCounts;
    }
}
