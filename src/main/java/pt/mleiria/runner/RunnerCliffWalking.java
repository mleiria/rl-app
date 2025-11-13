package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.agent.SARSAAgent;
import pt.mleiria.rl.mdp.env.CliffWalkingEnvironment;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.server.VisualizerServer;

import java.util.List;

import static pt.mleiria.runner.MainEvaluator.evaluateAgent;
import static pt.mleiria.runner.MainEvaluator.trainAgent;

public class RunnerCliffWalking {

    public static void main(String[] args) throws InterruptedException {
        int episodes = 100000; // Number of episodes to train the agents

        // Start the visualizer server on a specific port
        VisualizerServer server = new VisualizerServer(8887);
        server.start();

        // --- Run on CliffWalking Environment ---
        System.out.println("    RUNNING ON CLIFF WALKING ENVIRONMENT         ");
        System.out.println("=================================================");
        Environment cliffEnv = new CliffWalkingEnvironment();
        System.out.printf("Environment: Cliff Walking | States: %d | Actions: %d%n", cliffEnv.getNumStates(), cliffEnv.getNumActions());
        System.out.printf("Training for %d episodes.%n", episodes);

        Agent qLearningAgentCliff = new QLearningAgent(cliffEnv.getNumStates(), cliffEnv.getNumActions());
        Agent sarsaAgentCliff = new SARSAAgent(cliffEnv.getNumStates(), cliffEnv.getNumActions());
        List<Agent> agents = List.of(sarsaAgentCliff);
        for (Agent agent : agents) {
            System.out.println("Initialized Agent: " + agent.getName());
            final AgentResult result = trainAgent(agent, cliffEnv, episodes);
            evaluateAgent(agent.getQTable(), cliffEnv, server);
            PrintUtils.printGridPolicy(agent, cliffEnv);
            PrintUtils.printRewardSummary(result, agent.getName());
        }

        // Stop the server (optional, but good practice)
        try {
            server.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
