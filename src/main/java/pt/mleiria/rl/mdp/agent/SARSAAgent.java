package pt.mleiria.rl.mdp.agent;

public class SARSAAgent extends  BaseAgent {
    /**
     * Constructs a SARSA agent with specified parameters.
     *
     * @param numStates  The number of states in the environment.
     * @param numActions The number of actions available to the agent.
     */
    public SARSAAgent(final int numStates, final int numActions) {
        super("SARSA", numStates, numActions, 0.5, 0.99, 0.1);
    }

    @Override
    public void update(int state, int action, double reward, int nextState, int nextAction) {
        // Uses the Q-Value of the action that was actually chosen for the next state
        final double nextQ = qTable[nextState][nextAction];

        // Update the Q-value for the current state-action pair using the SARSA formula
        // qTable[state][action] += alpha * (reward + gamma * nextQ - qTable[state][action]);
        final double tdTarget = reward + gamma * nextQ;
        final double tdError = tdTarget - qTable[state][action];
        qTable[state][action] += alpha * tdError;
    }


}
