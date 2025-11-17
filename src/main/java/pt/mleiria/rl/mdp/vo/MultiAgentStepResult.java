package pt.mleiria.rl.mdp.vo;

public record MultiAgentStepResult(int nextState1, int nextState2, double[] rewards, boolean done) {
}
