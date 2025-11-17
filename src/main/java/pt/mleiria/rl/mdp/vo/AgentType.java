package pt.mleiria.rl.mdp.vo;

public enum AgentType {
    Q_LEARNING,
    SARSA,
    BOLTZMANN;

    public static boolean isOnPolicy(AgentType agentType) {
        return agentType == SARSA;
    }
}
