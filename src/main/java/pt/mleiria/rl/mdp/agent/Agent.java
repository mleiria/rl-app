package pt.mleiria.rl.mdp.agent;

public interface Agent {
    /**
     * Chooses an action based on the current state.
     *
     * @param state The current state of the environment.
     * @return The action to be taken.
     */
    int chooseAction(int state);

    /**
     * Updates the agent's knowledge based on the state, action, reward, next state, and next action.
     *
     * @param state      The current state.
     * @param action     The action taken.
     * @param reward     The reward received.
     * @param nextState  The next state after taking the action.
     * @param nextAction The action chosen in the next state.
     */
    void update(int state, int action, double reward, int nextState, int nextAction);

    /**
     * Returns the Q-table used by the agent.
     *
     * @return A 2D array representing the Q-table, where rows correspond to states and columns to actions.
     */
    double[][] getQTable();

    /**
     * Returns the name of the agent.
     *
     * @return The name of the agent.
     */
    String getName();
}
