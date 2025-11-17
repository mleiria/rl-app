package pt.mleiria.rl.mdp.agent;

import pt.mleiria.rl.mdp.vo.AgentType;

public class QLearningAgent extends BaseAgent {

    /**
     * Constructs a Q-Learning agent with specified parameters.
     *
     * @param numStates The number of states in the environment.
     * @param numActions The number of actions available to the agent.
     */
    public QLearningAgent(int numStates, int numActions){
        super(AgentType.Q_LEARNING, numStates, numActions, 0.5, 0.99, 0.1);
    }

    /**
     * Updates the Q-value for the given state-action pair using the Q-learning update rule.
     *
     * @param state      The current state.
     * @param action     The action taken.
     * @param reward     The reward received.
     * @param nextState  The next state after taking the action.
     * @param nextAction The action chosen in the next state (not used in Q-learning).
     */
    @Override
    public void update(int state, int action, double reward, int nextState, int nextAction) {
        updateQLearning(state, action, reward, nextState, nextAction);
    }
}
