package pt.mleiria.rl.mdp.agent;

public class QLearningAgent extends BaseAgent {

    /**
     * Constructs a Q-Learning agent with specified parameters.
     *
     * @param numStates The number of states in the environment.
     * @param numActions The number of actions available to the agent.
     */
    public QLearningAgent(int numStates, int numActions){
        super("Q-Learning", numStates, numActions, 0.5, 0.99, 0.1);
    }


    @Override
    public void update(int state, int action, double reward, int nextState, int nextAction) {
        // Find the maximum Q-value for the next state (ignores the actual next action)
        double maxNextQ = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < numActions; a++) {
            if (qTable[nextState][a] > maxNextQ) {
                maxNextQ = qTable[nextState][a];
            }
        }
        // Update the Q-value for the current state-action pair using the Q-learning formula
        // qTable[state][action] += alpha * (reward + gamma * maxNextQ - qTable[state][action]);
        final double tdTarget = reward + gamma * maxNextQ;
        final double tdError = tdTarget - qTable[state][action];
        qTable[state][action] += alpha * tdError;
    }
}
