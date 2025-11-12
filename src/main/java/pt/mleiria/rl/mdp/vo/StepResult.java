package pt.mleiria.rl.mdp.vo;

/**
 * Represents the result of taking a step in the environment.
 * Contains the next state, the reward received, and whether the episode is done.
 */
public record StepResult(int nextState, double reward, boolean done) {
}
