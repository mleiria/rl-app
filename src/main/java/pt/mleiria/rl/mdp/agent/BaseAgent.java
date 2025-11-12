package pt.mleiria.rl.mdp.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BaseAgent is an abstract class that implements the Agent interface.
 * It provides a foundation for reinforcement learning agents with common functionalities such as
 * Q-table management, action selection, and basic parameters for learning.
 */
abstract class BaseAgent implements Agent {
    protected final String name;
    protected final double[][] qTable;
    protected final int numActions;
    protected final double alpha; // Learning rate
    protected final double gamma; // Discount factor
    protected final double epsilon; // Exploration rate
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
    public BaseAgent(String name, int numStates, int numActions, double alpha, double gamma, double epsilon) {
        this.name = name;
        this.numActions = numActions;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new double[numStates][numActions];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double[][] getQTable() {
        return qTable;
    }

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
            }else if(qTable[state][action] == maxQ) {
                // If we find a tie, we add the action to the list of ties
                ties.add(action);
            }
        }
        // If there are ties, randomly select one of them
        return ties.get(random.nextInt(ties.size()));
    }
}
