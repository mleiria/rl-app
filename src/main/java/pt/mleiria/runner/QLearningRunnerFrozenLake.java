package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.FrozenLakeEnvironment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.server.VisualizerServer;

import static pt.mleiria.runner.MainEvaluator.evaluateAgent;
import static pt.mleiria.runner.MainEvaluator.trainAgent;

public class QLearningRunnerFrozenLake {



    public static void main(String[] args) throws InterruptedException {
        // Start the visualizer server on a specific port
        VisualizerServer server = new VisualizerServer(8887);
        server.start();
        int episodes = 100000; // Increased episodes for better learning in FrozenLake

        System.out.println("=================================================");
        System.out.println("  RUNNING Q-LEARNING ON FROZEN LAKE ENVIRONMENT  ");
        System.out.println("=================================================");


        Environment frozenEnv = new FrozenLakeEnvironment();

        Agent qLearningAgentFrozen = new QLearningAgent(frozenEnv.getNumStates(), frozenEnv.getNumActions());

        System.out.println("Training Q-Learning Agent on Frozen Lake for " + episodes + " episodes...");
        AgentResult qResultFrozen = trainAgent(qLearningAgentFrozen, frozenEnv, episodes);
        evaluateAgent(qResultFrozen.qTable(), frozenEnv, server);
        System.out.println("\nTraining Complete. Displaying Results:");
        PrintUtils.printGridPolicy(qLearningAgentFrozen, frozenEnv);
        PrintUtils.printRewardSummary(qResultFrozen, qLearningAgentFrozen.getName());
        // Stop the server (optional, but good practice)
        try {
            server.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
