package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.Map;

public interface Environment {
    /**
     * Returns the number of states in the environment.
     *
     * @return The number of states.
     */
    int getNumStates();

    /**
     * Returns the number of actions available in the environment.
     *
     * @return The number of actions.
     */
    int getNumActions();

    /**
     * Resets the environment to the initial state.
     *
     * @return The initial state of the environment.
     */
    int reset();

    /**
     * Takes an action in the environment and returns the result.
     *
     * @param action The action to take.
     * @return A StepResult containing the next state, reward, and whether the episode is done.
     */
    StepResult step(int action);

    // Provides details for visualization or debugging

    int getGridRows();

    int getGridCols();

    /**
     * Returns a map of special states in the environment with their descriptions.
     *
     * @return A map where keys are state indices and values are descriptions of the special states.
     */
    Map<Integer, String> getSpecialStates();


}
