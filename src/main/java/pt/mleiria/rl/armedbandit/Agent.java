package pt.mleiria.rl.armedbandit;

/**
 * An interface defining the essential methods for any bandit algorithm agent.
 */
interface Agent {
    /**
     * Chooses an arm to pull based on the agent's strategy.
     *
     * @return The index of the arm to pull.
     */
    int chooseArm();

    /**
     * Updates the agent's internal knowledge with the reward received from pulling an arm.
     *
     * @param armIndex The arm that was pulled.
     * @param reward   The reward that was received (1 for a win, 0 for a loss).
     */
    void update(int armIndex, int reward);

    /**
     * Returns the agent's final estimated values for each arm.
     *
     * @return An array of doubles representing the estimated values.
     */
    double[] getEstimatedValues();

    /**
     * Returns the agent's count of how many times each arm was pulled.
     *
     * @return An array of integers with the pull counts.
     */
    int[] getArmPullCounts();
}

