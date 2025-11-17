package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TaxiEnvironment implements Environment {

    private static final int GRID_ROWS = 5; // Number of rows in the grid
    private static final int GRID_COLS = 5; // Number of columns in the grid
    private static final int NUM_STATES = 500; // Total number of states in the grid
    private static final int NUM_ACTIONS = 6; // Number of actions (up, down, left, right, pickup, dropoff)

    // Coordinates for the 4 special locations R, G, Y, B
    private static final int[][] LOCATIONS = {
            {0, 0}, // 0: Red location
            {0, 4}, // 1: Green location
            {4, 0}, // 2: Yellow location
            {4, 3}  // 3: Blue location
    };

    // Internal state variables
    private int taxiRow; // Taxi's current row
    private int taxiCol; // Taxi's current column
    private int passengerLocationIdx; // Location of the passenger (0-3 for R, G, Y, B); 4 for "in taxi"
    private int destinationIdx;

    private final Random random = new Random();

    public TaxiEnvironment() {
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
     * Resets the environment to a new random episode.
     *
     * @return the encoded initial state.
     */
    @Override
    public int reset() {
        this.taxiRow = random.nextInt(GRID_ROWS);
        this.taxiCol = random.nextInt(GRID_COLS);
        //this.passengerLocationIdx = 2; // Fixed passenger location for testing
        //this.destinationIdx = 4; // Fixed destination for testing

        this.passengerLocationIdx = random.nextInt(LOCATIONS.length); // Randomly choose a passenger location
        // Ensure destination is different from passenger location
        do {
            this.destinationIdx = random.nextInt(LOCATIONS.length);
        } while (this.destinationIdx == this.passengerLocationIdx);

        return encodeState();
    }

    /**
     * Encodes the current state (row,col, pass_idx, dest_idx) of the environment into a single integer.
     *
     * @return the encoded state as an integer.
     */
    private int encodeState() {
        int state = this.destinationIdx;
        state += this.passengerLocationIdx * 4; // 4 possible passenger locations
        state += this.taxiCol * 4 * 5; // 5 rows, 4 columns
        state += this.taxiRow * 4 * 5 * 5; // 5 rows, 5 columns, 4 passenger locations
        return state;
    }

    /**
     * Manually sets the environment to a specific, deterministic state.
     * This is useful for testing and debugging.
     *
     * @param taxiRow The desired row for the taxi.
     * @param taxiCol The desired column for the taxi.
     * @param passIdx The desired passenger location index (0-4).
     * @param destIdx The desired destination index (0-3).
     * @return The encoded state for this specific configuration.
     */
    public int setState(int taxiRow, int taxiCol, int passIdx, int destIdx) {
        this.taxiRow = taxiRow;
        this.taxiCol = taxiCol;
        this.passengerLocationIdx = passIdx;
        this.destinationIdx = destIdx;
        return encodeState();
    }

    @Override
    public StepResult step(final int action) {
        double reward = -1.0; // Default reward for each step
        boolean done = false;

        switch (action) {
            case 0: // South (Down)
                this.taxiRow = Math.min(this.taxiRow + 1, GRID_ROWS - 1);
                break;
            case 1: // North (Up)
                this.taxiRow = Math.max(this.taxiRow - 1, 0);
                break;
            case 2: // East (Right)
                this.taxiCol = Math.min(this.taxiCol + 1, GRID_COLS - 1);
                break;
            case 3: // West (Left)
                this.taxiCol = Math.max(this.taxiCol - 1, 0);
                break;
            case 4: // Pickup
                // Check for illegal pickup
                if (this.passengerLocationIdx == 4 || // passenger already in taxi
                        this.taxiRow != LOCATIONS[this.passengerLocationIdx][0] || // taxi not at passenger location
                        this.taxiCol != LOCATIONS[this.passengerLocationIdx][1]) {
                    reward = -10.0; // Illegal action
                } else {
                    // Successful pickup
                    this.passengerLocationIdx = 4; // 4 means "in taxi"
                }
                break;
            case 5: // Dropoff
                // Check for illegal dropoff
                if (this.passengerLocationIdx != 4 || // passenger not in taxi
                        this.taxiRow != LOCATIONS[this.destinationIdx][0] || // taxi not at destination
                        this.taxiCol != LOCATIONS[this.destinationIdx][1]) {
                    reward = -10.0;
                } else {
                    // Successful dropoff
                    reward = 20.0; // Reward for successful dropoff
                    done = true; // Episode ends after dropoff
                }
                break;
        }
        // Encode the new state after taking the action
        int nextState = encodeState();

        return new StepResult(nextState, reward, done);
    }

    @Override
    public Map<Integer, String> getSpecialStates() {
        final Map<Integer, String> special = new HashMap<>();
        special.put(encodeLocation(0), "R"); // Red location
        special.put(encodeLocation(1), "G"); // Green location
        special.put(encodeLocation(2), "Y"); // Yellow location
        special.put(encodeLocation(3), "B"); // Blue location
        return special;
    }

    /**
     * Helper to get a grid index from a location index.
     * Encodes a location index (0-3) into a single integer representing the grid position.
     *
     * @param locIdx The index of the location (0 for R, 1 for G, 2 for Y, 3 for B).
     * @return The encoded location as an integer.
     */
    private Integer encodeLocation(int locIdx) {
        return LOCATIONS[locIdx][0] * GRID_COLS + LOCATIONS[locIdx][1];

    }

    public int getTaxiRow() {
        return taxiRow;
    }

    public int getTaxiCol() {
        return taxiCol;
    }

    public int getDestinationIdx() {
        return destinationIdx;
    }

    public int getPassengerLocationIdx() {
        return passengerLocationIdx;
    }
}
