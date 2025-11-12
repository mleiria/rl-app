package pt.mleiria.rl.armedbandit;

import java.util.Random;

/**
 * An agent that follows the Epsilon-Greedy strategy.
 */
class EpsilonGreedyAgent implements Agent {
    private final double epsilon;
    private final int numArms;
    private final int[] armPullCounts;
    private final double[] estimatedValues;
    private final Random random;

    public EpsilonGreedyAgent(int numArms, double epsilon) {
        this.numArms = numArms;
        this.epsilon = epsilon;
        this.armPullCounts = new int[numArms];
        this.estimatedValues = new double[numArms];
        this.random = new Random();
    }

    @Override
    public int chooseArm() {
        if (random.nextDouble() < this.epsilon) {
            // Explore: choose a random arm
            return random.nextInt(this.numArms);
        } else {
            // Exploit: choose the best-known arm
            return findBestArm();
        }
    }

    @Override
    public void update(int armIndex, int reward) {
        this.armPullCounts[armIndex]++;
        int n = this.armPullCounts[armIndex];
        double oldValue = this.estimatedValues[armIndex];
        // Incremental average formula: new_avg = old_avg + (1/n) * (new_value - old_avg)
        this.estimatedValues[armIndex] = oldValue + (1.0 / n) * (reward - oldValue);
    }

    private int findBestArm() {
        int bestArm = 0;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < this.numArms; i++) {
            if (this.estimatedValues[i] > maxVal) {
                maxVal = this.estimatedValues[i];
                bestArm = i;
            }
        }
        return bestArm;
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
