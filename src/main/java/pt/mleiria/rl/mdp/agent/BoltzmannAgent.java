package pt.mleiria.rl.mdp.agent;

import pt.mleiria.rl.mdp.vo.AgentType;

public class BoltzmannAgent extends BaseAgent {

    // New hyperparameter for Boltzmann
    private double temperature = 1.0; // Initial temperature (can be tuned)
    private double minTemperature = 0.01;
    private double temperatureDecayRate = 0.0005;


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
    public BoltzmannAgent(String name, int numStates, int numActions, double alpha, double gamma, double epsilon) {
        super(AgentType.BOLTZMANN, numStates, numActions, alpha, gamma, epsilon);
    }

    @Override
    public void update(int state, int action, double reward, int nextState, int nextAction) {
        updateQLearning(state, action, reward, nextState, nextAction);
    }

    /**
     * Chooses an action based on the current state using the Boltzmann exploration strategy.
     * This method converts Q-values into a probability distribution and samples from it.
     *
     * @param state The current state of the environment.
     * @return The action to be taken.
     */
    @Override
    public int chooseAction(int state) {
        // 1. Get the Q-values for the current state.
        double[] qValues = qTable[state];

        // 2. Calculate the exponentiated Q-values, scaled by the temperature.
        // We add a numerical stability trick here by subtracting the max Q-value
        // to prevent large values from causing an overflow in Math.exp().
        double maxQ = Double.NEGATIVE_INFINITY;
        for (double q : qValues) {
            if (q > maxQ) {
                maxQ = q;
            }
        }

        double[] expValues = new double[numActions];
        double sumOfExpValues = 0.0;
        for (int i = 0; i < numActions; i++) {
            // Subtracting maxQ prevents overflow and doesn't change the final probabilities
            expValues[i] = Math.exp((qValues[i] - maxQ) / temperature);
            sumOfExpValues += expValues[i];
        }

        // 3. Convert the exponentiated values into a probability distribution.
        double[] probabilities = new double[numActions];
        for (int i = 0; i < numActions; i++) {
            probabilities[i] = expValues[i] / sumOfExpValues;
        }

        // 4. Sample an action from the calculated probability distribution.
        // This is done by checking where a random number falls in the cumulative distribution.
        double cumulativeProb = 0.0;
        double rand = random.nextDouble();

        for (int action = 0; action < numActions; action++) {
            cumulativeProb += probabilities[action];
            if (rand <= cumulativeProb) {
                return action;
            }
        }
        // Fallback in case of floating point errors, return the last action.
        return numActions - 1;
    }

    /**
     * Call this method at the end of each training episode to decay the temperature.
     * This makes the agent's choices less random and more greedy over time.
     *
     * @param episode The current episode number.
     */
    public void decayTemperature(int episode) {
        temperature = minTemperature + (1.0 - minTemperature) * Math.exp(-temperatureDecayRate * episode);
    }
}
