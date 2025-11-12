package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CliffWalkingEnvironment implements Environment {
    private static final int N_ROWS = 4; // Number of rows in the grid
    private static final int N_COLS = 12; // Number of columns in the grid
    private static final int NUM_STATES = N_ROWS * N_COLS; // Total number of states in the grid
    private static final int NUM_ACTIONS = 4; // Number of actions (up, down, left, right)
    private static final int START_STATE = 36; // Starting state (row 3, column 0)
    private static final int GOAL_STATE = 47; // Goal state (row 3, column 11)
    private static final Set<Integer> CLIFF_STATES = new HashSet<>();

    static {
        for (int i = 37; i < 47; i++) {
            CLIFF_STATES.add(i);
        }
    }

    private int currentState;

    public CliffWalkingEnvironment() {
        this.currentState = START_STATE; // Initialize to the starting state
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
        this.currentState = START_STATE;
        return this.currentState;
    }

    @Override
    public StepResult step(int action) {
        int row = currentState / N_COLS;
        int col = currentState % N_COLS;

        if (action == 0) row = Math.max(0, row - 1);
        else if (action == 1) col = Math.min(N_COLS - 1, col + 1);
        else if (action == 2) row = Math.min(N_ROWS - 1, row + 1);
        else if (action == 3) col = Math.max(0, col - 1);

        this.currentState = row * N_COLS + col;

        if (CLIFF_STATES.contains(this.currentState)) {
            // Fell off the cliff, send back to start
            this.currentState = START_STATE;
            return new StepResult(this.currentState, -100.0, true);
        }

        if (this.currentState == GOAL_STATE) {
            return new StepResult(this.currentState, -1.0, true);
        }

        return new StepResult(this.currentState, -1.0, false);
    }

    @Override
    public int getGridRows() {
        return N_ROWS;
    }

    @Override
    public int getGridCols() {
        return N_COLS;
    }

    @Override
    public Map<Integer, String> getSpecialStates() {
        final Map<Integer, String> special = new HashMap<>();
        special.put(START_STATE, "S");
        special.put(GOAL_STATE, "G");
        CLIFF_STATES.forEach(c -> special.put(c, "C"));
        return special;
    }
}
