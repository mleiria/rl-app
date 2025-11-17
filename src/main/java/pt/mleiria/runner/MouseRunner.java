package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.agent.SARSAAgent;
import pt.mleiria.rl.mdp.env.CliffWalkingEnvironment;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.MouseEnvironment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.server.VisualizerServer;

import java.util.List;

import static pt.mleiria.runner.MainEvaluator.evaluateAgent;
import static pt.mleiria.runner.MainEvaluator.trainAgent;

public class MouseRunner {
    public static void main(String[] args) throws InterruptedException {
        int episodes = 1000; // Number of episodes to train the agents

        // Start the visualizer server on a specific port
        VisualizerServer server = new VisualizerServer(8887);
        server.start();

        // --- Run on CliffWalking Environment ---
        System.out.println("    RUNNING ON MOUSE MAZE ENVIRONMENT         ");
        System.out.println("=================================================");
        Environment mouseEnv = new MouseEnvironment();
        System.out.printf("Environment: Mouse Maze | States: %d | Actions: %d%n", mouseEnv.getNumStates(), mouseEnv.getNumActions());
        System.out.printf("Training for %d episodes.%n", episodes);

        Agent qLearningAgentMouse = new QLearningAgent(mouseEnv.getNumStates(), mouseEnv.getNumActions());
        List<Agent> agents = List.of(qLearningAgentMouse);
        for (Agent agent : agents) {
            System.out.println("Initialized Agent: " + agent.getName());
            final AgentResult result = trainAgent(agent, mouseEnv, episodes, server);
            evaluateAgent(agent.getQTable(), mouseEnv, server);
            PrintUtils.printGridPolicy(agent, mouseEnv);
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
