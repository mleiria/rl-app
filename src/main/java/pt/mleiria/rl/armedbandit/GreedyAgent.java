package pt.mleiria.rl.armedbandit;

import java.util.Arrays;

/**
 * An agent that follows the Epsilon-Greedy strategy.
 */
class GreedyAgent implements Agent {
    private final int numArms;
    private final int[] armPullCounts;
    private final double[] estimatedValues;

    /**
     * Constructs a GreedyAgent with a specified number of arms
     *
     * @param numArms the number of arms available to the agent
     */
    public GreedyAgent(int numArms) {
        this(numArms, 0.0); // Default optimistic initial value is 0.0
    }
    /**
     * Constructs a GreedyAgent with a specified number of arms and an optimistic initial value.
     *
     * @param numArms the number of arms available to the agent
     * @param optimisticInitialValue the initially estimated value for each arm
     */
    public GreedyAgent(int numArms, final double optimisticInitialValue) {
        this.numArms = numArms;
        this.armPullCounts = new int[numArms];
        this.estimatedValues = new double[numArms];
        Arrays.fill(estimatedValues, optimisticInitialValue);
    }

    @Override
    public int chooseArm() {
        // Exploit: choose the best-known arm
        return findBestArm();
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
