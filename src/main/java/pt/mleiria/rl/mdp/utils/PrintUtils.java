package pt.mleiria.rl.mdp.utils;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.vo.AgentResult;

import java.util.List;
import java.util.Map;

public class PrintUtils {
    public static void printGridPolicy(Agent agent, Environment env) {
        double[][] qTable = agent.getQTable();
        int rows = env.getGridRows();
        int cols = env.getGridCols();
        Map<Integer, String> specialStates = env.getSpecialStates();
        String[] actionSymbols = {"↑", "→", "↓", "←"};

        System.out.println("--- " + agent.getName() + " Policy ---");
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int stateIdx = r * cols + c;
                if (specialStates.containsKey(stateIdx)) {
                    sb.append(String.format(" %-2s", specialStates.get(stateIdx)));
                } else {
                    int bestAction = findBestAction(qTable, stateIdx);
                    sb.append(String.format(" %-2s", actionSymbols[bestAction]));
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private static int findBestAction(double[][] qTable, int state) {
        int bestAction = 0;
        double maxQ = Double.NEGATIVE_INFINITY;
        for (int action = 0; action < qTable[state].length; action++) {
            if (qTable[state][action] > maxQ) {
                maxQ = qTable[state][action];
                bestAction = action;
            }
        }
        return bestAction;
    }

    public static void printRewardSummary(AgentResult result, String agentName) {
        List<Double> rewards = result.episodeRewards();
        double avgReward = rewards.stream().mapToDouble(d -> d).average().orElse(0.0);
        double last100Avg = rewards.subList(Math.max(0, rewards.size() - 100), rewards.size())
                .stream().mapToDouble(d -> d).average().orElse(0.0);

        System.out.printf("--- %s Performance ---\n", agentName);
        System.out.printf("Average reward over all episodes: %.2f\n", avgReward);
        System.out.printf("Average reward over last 100 episodes: %.2f\n\n", last100Avg);
    }
}
