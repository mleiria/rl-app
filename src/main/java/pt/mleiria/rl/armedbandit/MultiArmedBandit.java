package pt.mleiria.rl.armedbandit;


import java.util.Random;

/**
 * Represents the multi-armed bandit environment.
 */
class MultiArmedBandit {
    private final double[] probabilities;
    private final Random random;

    public MultiArmedBandit(double[] probabilities) {
        if (probabilities == null || probabilities.length == 0) {
            throw new IllegalArgumentException("Probabilities cannot be null or empty.");
        }
        this.probabilities = probabilities;
        this.random = new Random();
    }

    public int getNumArms() {
        return this.probabilities.length;
    }

    /**
     * Simulates pulling an arm and returns a reward.
     *
     * @param armIndex The index of the arm to pull.
     * @return 1 for a win, 0 for a loss.
     */
    public int pullArm(int armIndex) {
        if (armIndex < 0 || armIndex >= getNumArms()) {
            throw new IllegalArgumentException("Invalid arm index.");
        }
        return random.nextDouble() < this.probabilities[armIndex] ? 1 : 0;
    }
}