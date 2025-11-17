package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.agent.SARSAAgent;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.TaxiEnvironment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.server.VisualizerServer;

import static pt.mleiria.runner.MainEvaluator.evaluateAgent;
import static pt.mleiria.runner.MainEvaluator.trainAgent;

public class RunnerTaxiDriver {

    public static void main(String[] args) throws InterruptedException {
        // Start the visualizer server on a specific port
        VisualizerServer server = new VisualizerServer(8887);
        server.start();
        int episodes = 1000000; // Number of episodes to train the agents

        System.out.println("\n=================================================");
        System.out.println("        RUNNING ON TAXI ENVIRONMENT              ");
        System.out.println("=================================================");
        Environment taxiEnv = new TaxiEnvironment();

        // Agents need to know the state and action space sizes of the new environment
        Agent qLearningAgentTaxi = new QLearningAgent(taxiEnv.getNumStates(), taxiEnv.getNumActions());
        Agent sarsaAgentTaxi = new SARSAAgent(taxiEnv.getNumStates(), taxiEnv.getNumActions());

        // Train agents (Taxi might need more episodes due to its larger state space)
        AgentResult qResultTaxi = trainAgent(qLearningAgentTaxi, taxiEnv, episodes);
        evaluateAgent(qResultTaxi.qTable(), taxiEnv, server);
        //AgentResult sarsaResultTaxi = trainAgent(sarsaAgentTaxi, taxiEnv, taxiEpisodes);

        // The generic PrintUtils.printGridPolicy won't be very meaningful for Taxi,
        // as the optimal action depends on passenger/destination state, not just location.
        // We will rely on the reward summary for evaluation.

        PrintUtils.printRewardSummary(qResultTaxi, qLearningAgentTaxi.getName());
        //PrintUtils.printRewardSummary(sarsaResultTaxi, sarsaAgentTaxi.getName());
        // Stop the server (optional, but good practice)
        try {
            server.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
