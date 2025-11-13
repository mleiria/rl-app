package pt.mleiria.rl.mdp.vo;

public record AgentStatus(int agentPosition, int epoch, int stepCount, double totalReward) {
}
