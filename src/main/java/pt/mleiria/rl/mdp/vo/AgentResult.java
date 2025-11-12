package pt.mleiria.rl.mdp.vo;

import java.util.List;
/**
 * Represents the result of an agent's interaction with the environment.
 * Contains the Q-table and a list of rewards collected during episodes.
 */
public record AgentResult(double[][] qTable, List<Double> episodeRewards) {
}
