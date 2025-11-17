package pt.mleiria.rl.mdp.agent;

import pt.mleiria.rl.mdp.vo.AgentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BaseAgent is an abstract class that implements the Agent interface.
 * It provides a foundation for reinforcement learning agents with common functionalities such as
 * Q-table management, action selection, and basic parameters for learning.
 */
abstract class BaseAgent implements Agent {
    protected final AgentType name;
    protected final double[][] qTable;
    protected final int numActions;
    protected final double alpha; // Learning rate
    protected final double gamma; // Discount factor
    protected double epsilon; // Exploration rate
    protected final double epsilonMin = 0.01;
    protected final double epsilonDecay = 0.995;
    protected final Random random = new Random();

    /**
     * Constructs a BaseAgent with specified parameters.
     *
     * @param name       The name of the agent.
     * @param numStates  The number of states in the environment.
     * @param numActions The number of actions available to the agent.
     * @param alpha      The learning rate.
     * @param gamma      The discount factor.
     * @param epsilon    The exploration rate.
     */
    public BaseAgent(AgentType name, int numStates, int numActions, double alpha, double gamma, double epsilon) {
        this.name = name;
        this.numActions = numActions;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new double[numStates][numActions];
    }

    @Override
    public String getName() {
        return name.name();
    }

    @Override
    public double[][] getQTable() {
        return qTable;
    }

    /**
     * The algorithm a software agent uses to determine its actions is called its policy
     * Chooses an action based on the current state using the Epsilon-Greedy strategy.
     *
     * @param state The current state of the environment.
     * @return The action to be taken.
     */
    @Override
    public int chooseAction(int state) {
        if (random.nextDouble() < epsilon) {
            // Explore: choose a random action
            return random.nextInt(numActions);
        } else {
            // Exploit: choose the action with the highest Q-value for the current state
            return findBestAction(state);
        }
    }

    protected int findBestAction(int state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        final List<Integer> ties = new ArrayList<>();
        for (int action = 0; action < numActions; action++) {
            if (qTable[state][action] > maxQ) {
                maxQ = qTable[state][action];
                ties.clear();
                ties.add(action);
            } else if (qTable[state][action] == maxQ) {
                // If we find a tie, we add the action to the list of ties
                ties.add(action);
            }
        }
        // If there are ties, randomly select one of them
        return ties.get(random.nextInt(ties.size()));
    }

    /**
     * Updates the Q-value for the given state-action pair using the SARSA update rule.
     *
     * @param state      The current state.
     * @param action     The action taken.
     * @param reward     The reward received.
     * @param nextState  The next state after taking the action.
     * @param nextAction The action chosen in the next state.
     */
    public void updateSARSA(int state, int action, double reward, int nextState, int nextAction) {
        // Uses the Q-Value of the action that was actually chosen for the next state
        final double nextQ = qTable[nextState][nextAction];

        // Update the Q-value for the current state-action pair using the SARSA formula
        // qTable[state][action] += alpha * (reward + gamma * nextQ - qTable[state][action]);
        final double tdTarget = reward + gamma * nextQ;
        final double tdError = tdTarget - qTable[state][action];
        qTable[state][action] += alpha * tdError;
    }

    protected void updateQLearning(int state, int action, double reward, int nextState, int nextAction) {
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

    @Override
    public void reduceEpsilon() {
        if (epsilon > epsilonMin) {
            epsilon *= epsilonDecay;
        }
    }
}
