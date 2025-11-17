package pt.mleiria.rl.mdp.vo;

public record MouseAgentStatus(AgentStatus agentStatus, int hasEaten, int hasDrunk,
                               int foodState1D, int waterState1D) {
}
