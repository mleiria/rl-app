package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Random; // Added import for Random

/**
 * Represents a Frozen Lake environment for reinforcement learning.
 * This class implements the Environment interface and provides methods
 * to interact with the Frozen Lake environment.
 */
public class FrozenLakeEnvironment implements Environment {

    private static final int NUM_STATES = 16; // Example number of states
    private static final int NUM_ACTIONS = 4; // Example number of actions (up, down, left, right)
    private static final int GRID_SIZE = 4; // Example grid size (4x4)
    private static final Set<Integer> HOLES = Set.of(5, 7, 11, 12); // Example hole positions
    private static final int GOAL_STATE = 15; // Example goal state (bottom-right corner)

    private int currentState;
    private final Random random; // Added Random instance

    public FrozenLakeEnvironment() {
        this.currentState = 0; // Start at the initial state
        this.random = new Random(); // Initialize Random
    }

    @Override
    public int getNumStates() {
        return NUM_STATES;
    }

    @Override
    public int getNumActions() {
        return NUM_ACTIONS;
    }

    @Override
    public int reset() {
        this.currentState = 0; // Reset to the initial state
        return this.currentState;
    }

    /**
     * Simulates a step in the Frozen Lake environment based on the agent's action.
     * This method updates the environment's state, calculates the reward, and determines
     * if the episode has terminated.
     *
     * @param action The action chosen by the agent (0: Up, 1: Right, 2: Down, 3: Left).
     * @return A {@link StepResult} object containing the next state, the reward received,
     *         and a boolean indicating if the episode is done.
     *
     * <p><b>Mechanics:</b></p>
     * <ol>
     *     <li><b>Calculate Intended Next Position:</b>
     *         The method first converts the current state (a 1D index) into 2D grid
     *         coordinates (row and column). Then, based on the {@code action} taken,
     *         it calculates the *intended* next row and column. It handles boundary
     *         conditions by ensuring the agent does not move off the grid.
     *     </li>
     *     <li><b>Update Current State:</b>
     *         The calculated row and column are then converted back into a single
     *         integer {@code currentState}, representing the agent's new position on the grid.
     *     </li>
     *     <li><b>Determine Reward and Termination:</b>
     *         <ul>
     *             <li>If the new state is a <b>HOLE</b>: The reward is {@code 0.0}, and the episode terminates ({@code done = true}).</li>
     *             <li>If the new state is the <b>GOAL_STATE</b>: The reward is {@code 1.0}, and the episode terminates ({@code done = true}).</li>
     *             <li>If the new state is neither a hole nor the goal (i.e., a <b>FROZEN</b> tile or the <b>START</b> tile): The reward is {@code 0.0}, and the episode continues ({@code done = false}).</li>
     *         </ul>
     *     </li>
     * </ol>
     * <p><b>Note on Stochasticity:</b></p>
     * <p>This specific implementation of the {@code FrozenLakeEnvironment} is <b>deterministic</b>.
     * The agent always moves exactly in the intended direction (or stays in place if it hits a boundary).
     * There is no random "slipping" to an adjacent tile, which is often a feature of the more
     * challenging stochastic Frozen Lake problem.</p>
     */
    @Override
    public StepResult step(int action) {
        int row = currentState / GRID_SIZE;
        int col = currentState % GRID_SIZE;
        //printMatrixState(row, col, action);

        if (action == 0) row = Math.max(0, row - 1);         // Up
        else if (action == 1) col = Math.min(GRID_SIZE - 1, col + 1); // Right
        else if (action == 2) row = Math.min(GRID_SIZE - 1, row + 1); // Down
        else if (action == 3) col = Math.max(0, col - 1);         // Left

        this.currentState = row * GRID_SIZE + col;

        if (HOLES.contains(this.currentState)) {
            return new StepResult(this.currentState, 0.0, true);
        }
        if (this.currentState == GOAL_STATE) {
            return new StepResult(this.currentState, 1.0, true);
        }

        return new StepResult(this.currentState, 0.0, false);
    }

    private void printMatrixState(final int row, final int col, final int action) {

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int idx = r * GRID_SIZE + c;
                if (r == row && c == col) {
                    System.out.print(" A ");
                } else if (idx == GOAL_STATE) {
                    System.out.print(" G ");
                } else if (HOLES.contains(idx)) {
                    System.out.print(" H ");
                } else if (idx == 0) {
                    System.out.print(" S ");
                } else {
                    System.out.print(" - ");
                }
            }
            System.out.println();
        }
        System.out.println();
        switch (action) {
            case 0 -> System.out.println("Action: UP");
            case 1 -> System.out.println("Action: RIGHT");
            case 2 -> System.out.println("Action: DOWN");
            case 3 -> System.out.println("Action: LEFT");
            default -> System.out.println("Action: UNKNOWN");
        }
    }


    @Override
    public int getGridRows() {
        return GRID_SIZE;
    }

    @Override
    public int getGridCols() {
        return GRID_SIZE;
    }

    @Override
    public Map<Integer, String> getSpecialStates() {
        final Map<Integer, String> special = new HashMap<>();
        special.put(0, "S");
        special.put(GOAL_STATE, "G");
        HOLES.forEach(h -> special.put(h, "H"));
        return special;
    }
}
