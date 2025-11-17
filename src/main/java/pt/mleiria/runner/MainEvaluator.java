package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.MouseEnvironment;
import pt.mleiria.rl.mdp.env.TaxiEnvironment;
import pt.mleiria.rl.mdp.vo.*;
import pt.mleiria.server.VisualizerServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainEvaluator {


    private static void handleProgressLogging(int episodeNum) {
        if (episodeNum % 100 == 0) {
            System.out.println("Episode " + episodeNum + " started.");
        }
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleVisualization(VisualizerServer server, Environment env, int state, int episode, int step, double reward) {
        if (env instanceof TaxiEnvironment taxiEnv) {
            // Modern Java (16+) instanceof pattern matching
            final int passengerLocation = taxiEnv.getPassengerLocationIdx();
            final int destinationLocation = taxiEnv.getDestinationIdx();
            final int geoSpace = taxiEnv.getTaxiRow() * 5 + taxiEnv.getTaxiCol();
            final AgentStatus agentStatus = new AgentStatus(geoSpace, episode, step, reward);
            final TaxiDriverAgentStatus taxiStatus = new TaxiDriverAgentStatus(agentStatus, passengerLocation, destinationLocation);
            server.sendState(taxiStatus);
        } else if (env instanceof MouseEnvironment mouseEnv) {
            final int hasEaten = mouseEnv.hasEaten() ? 1 : 0;
            final int hasDrunk = mouseEnv.hasDrunk() ? 1 : 0;
            final int foodState1D = mouseEnv.getFoodState1D();
            final int waterState1D = mouseEnv.getWaterState1D();
            final int geoSpace = mouseEnv.getAgentRow() * mouseEnv.getGridCols() + mouseEnv.getAgentCol();
            final AgentStatus agentStatus = new AgentStatus(geoSpace, episode, step, reward);
            final MouseAgentStatus mouseAgentStatus = new MouseAgentStatus(agentStatus, hasEaten, hasDrunk, foodState1D, waterState1D);
            server.sendState(mouseAgentStatus);
        } else {
            server.sendState(state, episode, step, reward);
        }
    }


    public static AgentResult trainAgent(Agent agent, Environment env, int episodes) throws InterruptedException {
        return trainAgent(agent, env, episodes, null);
    }

    /**
     * Trains the given agent on the specified environment for a number of episodes.
     * This method is duplicated from ReinforcementLearningRunner for self-containment,
     * but in a larger project, it would ideally be refactored into a common utility.
     *
     * @param agent    The agent to be trained.
     * @param env      The environment in which the agent operates.
     * @param episodes The number of episodes to train the agent.
     * @return An AgentResult containing the Q-table and rewards from each episode.
     */
    public static AgentResult trainAgent(Agent agent, Environment env, int episodes, VisualizerServer server) {


        final List<Double> episodeRewards = new ArrayList<>();

        for (int episode = 0; episode < episodes; episode++) {


            int state = env.reset();
            if (null != server) {
                handleVisualization(server, env, state, episode, 0, 0);
            }

            boolean done = false;
            double totalReward = 0;

            // The on-policy loop requires choosing the first action before the loop
            int action =
                    AgentType.isOnPolicy(AgentType.valueOf(agent.getName()))
                            ? agent.chooseAction(state)
                            : List.of(0, 1, 2, 3, 4, 5).get(new Random().nextInt(env.getNumActions()));

            handleProgressLogging(episode);
            int stepCount = 0;
            agent.reduceEpsilon();
            while (!done) {
                stepCount++;
                StepResult result = env.step(action);
                totalReward += result.reward();

                int nextAction = agent.chooseAction(result.nextState());

                agent.update(state, action, result.reward(), result.nextState(), nextAction);
                // Prepare for next step. The future becomes the present.
                state = result.nextState();
                action = nextAction;
                done = result.done();
                //PrintUtils.printGridPolicy(agent, env);
                if (null != server) {
                    handleVisualization(server, env, state, episode, stepCount, totalReward);
                    sleep(100);
                }
            }
            episodeRewards.add(totalReward);
            //PrintUtils.printGridPolicy(agent, env);
        }
        System.out.println("Episode finished. You can close the browser.");

        return new AgentResult(agent.getQTable(), episodeRewards);
    }
// Assume qTable is your trained Q-table from the training process
// double[][] qTable = your_trained_agent.getQTable();

    public static void evaluateAgent(double[][] qTable, Environment env) throws InterruptedException {
        evaluateAgent(qTable, env, null);
    }

    public static void evaluateAgent(double[][] qTable, Environment env, VisualizerServer server) {

        int totalEpisodes = 1000; // Run a large number of episodes for statistical significance
        int successes = 0;
        int totalStepsInSuccesses = 0;
        double totalReward = 0.0;

        System.out.println("--- Starting Quantitative Evaluation ---");

        for (int episode = 0; episode < totalEpisodes; episode++) {
            int state = env.reset();
            boolean done = false;
            int steps = 0;

            // Set a max step limit to prevent infinite loops if the agent learned a bad policy
            int maxStepsPerEpisode = 999;
            handleProgressLogging(episode);
            while (!done && steps < maxStepsPerEpisode) {
                // *** IMPORTANT: NO EXPLORATION (EPSILON = 0) ***
                // Choose the best action from the Q-table for the current state.
                int action = getBestAction(qTable, state);

                StepResult result = env.step(action);

                state = result.nextState();
                done = result.done();
                if (null != server) {
                    handleVisualization(server, env, state, episode, steps, totalReward);
                }
                // Slow down the simulation so we can watch it
                sleep(100); // 500 milliseconds
                totalReward += result.reward();
                steps++;

                // If the episode ended and the reward is 1.0, it's a success
                if (done && result.reward() > 0.0) {
                    successes++;
                    totalStepsInSuccesses += steps;
                }
            }
        }

        // --- Calculate and Print the Metrics ---
        double successRate = (double) successes / totalEpisodes * 100.0;
        double avgSteps = (successes > 0) ? (double) totalStepsInSuccesses / successes : 0.0;
        double avgReward = totalReward / totalEpisodes;

        System.out.printf("Evaluation over %d episodes:\n", totalEpisodes);
        System.out.printf("  - Success Rate: %.2f%%\n", successRate);
        System.out.printf("  - Average Steps per Success: %.2f\n", avgSteps);
        System.out.printf("  - Average Reward per Episode: %.4f\n", avgReward);

        runBenchmarkTestTaxiDriver(qTable);
    }

    /**
     * Helper function to find the action with the highest Q-value for a given state.
     *
     * @param qTable The trained Q-table.
     * @param state  The current state.
     * @return The best action to take.
     */
    public static int getBestAction(double[][] qTable, int state) {
        double maxQValue = Double.NEGATIVE_INFINITY;
        int bestAction = -1;
        for (int action = 0; action < qTable[state].length; action++) {
            if (qTable[state][action] > maxQValue) {
                maxQValue = qTable[state][action];
                bestAction = action;
            }
        }
        // If all Q-values are the same (e.g., all zero), pick a random action to break ties.
        if (bestAction == -1) {
            return new Random().nextInt(qTable[state].length);
        }
        return bestAction;
    }

    public static void runBenchmarkTestTaxiDriver(double[][] qTable) {
        TaxiEnvironment env = new TaxiEnvironment();

        System.out.println("\n--- Starting Benchmark Test ---");

        // Benchmark 1: A difficult scenario (e.g., far pickup and dropoff)
        System.out.println("Benchmark 1: Pickup at Yellow (4,0), Dropoff at Green (0,4)");
        // Taxi starts at (2,2), Passenger at Y(2), Destination at G(1)
        int initialState = env.setState(2, 2, 2, 1);
        runSingleEpisode(env, qTable, initialState);

        // Benchmark 2: An easy scenario
        System.out.println("\nBenchmark 2: Pickup at Red (0,0), Dropoff at Green (0,4)");
        // Taxi starts near passenger
        initialState = env.setState(0, 1, 0, 1);
        runSingleEpisode(env, qTable, initialState);
    }

    /**
     * Helper function to run and print results for one specific episode.
     */
    private static void runSingleEpisode(TaxiEnvironment env, double[][] qTable, int state) {
        boolean done = false;
        int steps = 0;
        while (!done) {
            int action = getBestAction(qTable, state);
            StepResult result = env.step(action);
            state = result.nextState();
            done = result.done();
            steps++;
            // You can add visualization calls here if you want
        }
        System.out.printf("  -> Episode completed in %d steps.\n", steps);
    }
}
