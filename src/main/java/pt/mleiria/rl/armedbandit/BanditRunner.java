package pt.mleiria.rl.armedbandit;


import java.util.Arrays;
import java.util.List;

/**
 * The main class to run the bandit simulation.
 */
public class BanditRunner {

    public static void main(String[] args) {
        // --- Simulation Setup ---
        double[] trueProbabilities = {0.3, 0.6, 0.8};
        int numArms = trueProbabilities.length;
        int numSteps = 1000;

        // --- Agents Setup ---
        ArmedBanditAgent greedyAgent = new GreedyArmedBanditAgent(numArms);
        ArmedBanditAgent epsilonGreedyAgent = new EpsilonGreedyArmedBanditAgent(numArms, 0.1);
        ArmedBanditAgent ucbAgent = new UCBArmedBanditAgent(numArms, 2.0);
        ArmedBanditAgent boltzmannAgent = new BoltzmannArmedBanditAgent(numArms, 0.5);

        List<ArmedBanditAgent> agents = List.of(greedyAgent, epsilonGreedyAgent, ucbAgent, boltzmannAgent);
        List<String> agentNames = List.of("Greedy", "Epsilon-Greedy (ε=0.1)", "UCB (c=2)", "Boltzmann (τ=0.5)");

        // --- Run Simulation ---
        for (ArmedBanditAgent agent : agents) {
            MultiArmedBandit simulationBandit = new MultiArmedBandit(trueProbabilities); // Use a fresh bandit for each agent
            for (int step = 0; step < numSteps; step++) {
                int chosenArm = agent.chooseArm();
                int reward = simulationBandit.pullArm(chosenArm);
                agent.update(chosenArm, reward);
            }
        }

        // --- Print Final Results ---
        System.out.println("True Probabilities: " + Arrays.toString(trueProbabilities));
        System.out.println("------------------------------------------");

        for (int i = 0; i < agents.size(); i++) {
            String name = agentNames.get(i);
            ArmedBanditAgent agent = agents.get(i);

            // Format estimated values to 3 decimal places
            double[] estimatedValues = agent.getEstimatedValues();
            String[] formattedValues = new String[estimatedValues.length];
            for (int j = 0; j < estimatedValues.length; j++) {
                formattedValues[j] = String.format("%.3f", estimatedValues[j]);
            }

            System.out.println(name + " Final Estimated Values: " + Arrays.toString(formattedValues));
            System.out.println(name + " Arm Pull Counts: " + Arrays.toString(agent.getArmPullCounts()));
            System.out.println("------------------------------------------");
        }
    }
}
