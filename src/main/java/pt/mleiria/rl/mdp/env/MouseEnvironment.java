package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MouseEnvironment implements Environment{

    // --- Grid and Action Constants ---
    private static final int GRID_ROWS = 10;
    private static final int GRID_COLS = 10;
    private static final int NUM_ACTIONS = 4; // 0:N, 1:S, 2:E, 3:W

    // --- State Space Calculation ---
    // State is a combination of: agent_pos (100) * has_eaten (2) * has_drunk (2)
    private static final int NUM_STATES = GRID_ROWS * GRID_COLS * 2 * 2;

    // --- Special Locations ---
    private final int startState1D = 0; // Top-left corner
    private final int exitState1D = (GRID_ROWS * GRID_COLS) - 1; // Bottom-right corner
    private int foodState1D;
    private int waterState1D;

    // --- Internal State Variables ---
    private int agentRow;
    private int agentCol;
    private boolean hasEaten;
    private boolean hasDrunk;

    private final Random random = new Random();

    public MouseEnvironment() {
        reset();
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
    public int getGridRows() {
        return GRID_ROWS;
    }

    @Override
    public int getGridCols() {
        return GRID_COLS;
    }

    /**
     * Resets the environment to a new episode.
     * This places the agent at the start and randomly chooses new locations for food and water.
     *
     * @return The encoded initial state.
     */
    @Override
    public int reset() {
        // Reset agent position and status
        this.agentRow = 0;
        this.agentCol = 0;
        this.hasEaten = false;
        this.hasDrunk = false;
        //this.foodState1D = 13; // Fixed position for food
        //this.waterState1D = 76; // Fixed position for water

        // Choose new random locations for food and water
        // Ensure they are not on the start or exit squares

        do {
            this.foodState1D = random.nextInt(GRID_ROWS * GRID_COLS);
        } while (this.foodState1D == startState1D || this.foodState1D == exitState1D);

        do {
            this.waterState1D = random.nextInt(GRID_ROWS * GRID_COLS);
        } while (this.waterState1D == startState1D || this.waterState1D == exitState1D || this.waterState1D == this.foodState1D);

        return encodeState();
    }

    /**
     * Encodes the current state (row, col, hasEaten, hasDrunk) into a single integer.
     * This is the core of the state representation.
     *
     * @return The encoded state.
     */
    private int encodeState() {
        // The formula for encoding is like a multi-digit number system.
        // agent_pos is the least significant "digit", then hasEaten, then hasDrunk.
        int agentPos1D = this.agentRow * GRID_COLS + this.agentCol;
        int eatenBit = this.hasEaten ? 1 : 0;
        int drunkBit = this.hasDrunk ? 1 : 0;

        // Formula: pos + (100 * eaten_status) + (100 * 2 * drunk_status)
        int state = agentPos1D;
        state += (GRID_ROWS * GRID_COLS) * eatenBit;      // Offset by 100 if eaten
        state += (GRID_ROWS * GRID_COLS * 2) * drunkBit;  // Offset by 200 if drunk
        return state;
    }

    /**
     * Executes one time step within the environment.
     *
     * @param action The action to take (0:N, 1:S, 2:E, 3:W).
     * @return A StepResult containing the new state, reward, and done flag.
     */
    @Override
    public StepResult step(int action) {
        // Move the agent
        switch (action) {
            case 0: // North
                this.agentRow = Math.max(0, this.agentRow - 1);
                break;
            case 1: // South
                this.agentRow = Math.min(GRID_ROWS - 1, this.agentRow + 1);
                break;
            case 2: // East
                this.agentCol = Math.min(GRID_COLS - 1, this.agentCol + 1);
                break;
            case 3: // West
                this.agentCol = Math.max(0, this.agentCol - 1);
                break;
        }

        int currentPos1D = this.agentRow * GRID_COLS + this.agentCol;
        double reward = -1.0; // Small penalty for each step to encourage efficiency
        boolean done = false;

        // --- Check for rewards and state changes ---

        // 1. Check if agent is at the Food location
        if (currentPos1D == this.foodState1D && !this.hasEaten) {
            this.hasEaten = true;
            reward += 20.0; // Intermediate reward for finding food
        }

        // 2. Check if agent is at the Water location
        if (currentPos1D == this.waterState1D && !this.hasDrunk) {
            this.hasDrunk = true;
            reward += 20.0; // Intermediate reward for finding water
        }

        // 3. Check if agent is at the Exit
        if (currentPos1D == this.exitState1D) {
            if (this.hasEaten && this.hasDrunk) {
                reward += 50.0; // BIG reward for succeeding correctly
            } else {
                reward -= 50.0; // BIG penalty for reaching the exit unprepared
            }
            done = true; // Episode ends when the exit is reached, regardless of outcome
        }

        int nextState = encodeState();
        return new StepResult(nextState, reward, done);
    }

    // --- Helper methods for visualization or debugging ---

    @Override
    public Map<Integer, String> getSpecialStates() {
        final Map<Integer, String> special = new HashMap<>();
        special.put(startState1D, "S");
        special.put(exitState1D, "E");
        special.put(foodState1D, "F");
        special.put(waterState1D, "W");
        return special;
    }

    public int getAgentRow() {
        return agentRow;
    }

    public int getAgentCol() {
        return agentCol;
    }

    public boolean hasEaten() {
        return hasEaten;
    }

    public boolean hasDrunk() {
        return hasDrunk;
    }

    public int getFoodState1D() {
        return foodState1D;
    }

    public int getWaterState1D() {
        return waterState1D;
    }
}
