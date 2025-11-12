package pt.mleiria.rl.mdp.vo;

public record MultiAgentStepResult(int nextState1, int nextState2, double reward1, double reward2, boolean done) {
}
