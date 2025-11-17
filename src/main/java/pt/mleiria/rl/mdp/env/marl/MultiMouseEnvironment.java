package pt.mleiria.rl.mdp.env.marl;

import pt.mleiria.rl.mdp.vo.MultiAgentStepResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MultiMouseEnvironment { // Can't implement the single-agent Environment interface directly

    // --- Grid and Action Constants ---
    private static final int GRID_ROWS = 10;
    private static final int GRID_COLS = 10;
    private static final int NUM_AGENTS = 2;
    private static final int NUM_ACTIONS = 4; // 0:N, 1:S, 2:E, 3:W

    // --- State Space Calculation ---
    // State: (pos1 * pos2 * eaten1 * drunk1 * eaten2 * drunk2)
    private static final int NUM_STATES = (GRID_ROWS * GRID_COLS) * (GRID_ROWS * GRID_COLS) * 2 * 2 * 2 * 2;

    // --- Special Locations ---
    private final int startState1D = 0;
    private final int exitState1D = (GRID_ROWS * GRID_COLS) - 1;
    private int foodState1D;
    private int waterState1D;

    // --- Internal State Variables for BOTH agents ---
    private int agent1Row, agent1Col;
    private boolean agent1HasEaten, agent1HasDrunk;

    private int agent2Row, agent2Col;
    private boolean agent2HasEaten, agent2HasDrunk;

    private final Random random = new Random();

    public MultiMouseEnvironment() {
        reset();
    }

    public int getNumStates() {
        return NUM_STATES;
    }

    public int getNumActions() {
        return NUM_ACTIONS;
    }

    /**
     * Resets the environment. Both agents return to the start.
     * Food and water locations are randomized for the new episode.
     */
    public int reset() {
        // Reset agent 1
        this.agent1Row = 0;
        this.agent1Col = 0;
        this.agent1HasEaten = false;
        this.agent1HasDrunk = false;

        // Reset agent 2
        this.agent2Row = 0;
        this.agent2Col = 0;
        this.agent2HasEaten = false;
        this.agent2HasDrunk = false;

        // Randomize food/water (can be fixed for easier learning)
        // For this example, let's fix them to make the problem solvable.
        this.foodState1D = 27;
        this.waterState1D = 72;

        return encodeState();
    }

    /**
     * Encodes the complete state of the world into a single integer.
     */
    private int encodeState() {
        int pos1 = agent1Row * GRID_COLS + agent1Col;
        int pos2 = agent2Row * GRID_COLS + agent2Col;
        int e1 = agent1HasEaten ? 1 : 0;
        int d1 = agent1HasDrunk ? 1 : 0;
        int e2 = agent2HasEaten ? 1 : 0;
        int d2 = agent2HasDrunk ? 1 : 0;

        // Use a base-N encoding system where N is the size of each state variable
        int state = pos1;
        state = state * 100 + pos2;
        state = state * 2 + e1;
        state = state * 2 + d1;
        state = state * 2 + e2;
        state = state * 2 + d2;
        return state;
    }

    /**
     * The new step function takes an array of actions, one for each agent.
     *
     * @param actions actions[0] is for agent 1, actions[1] is for agent 2.
     * @return A result object containing the next state, rewards, and done flag.
     */
    public MultiAgentStepResult step(int[] actions) {
        // --- 1. Calculate potential next positions ---
        int[] nextRow = {agent1Row, agent2Row};
        int[] nextCol = {agent1Col, agent2Col};
        for (int i = 0; i < NUM_AGENTS; i++) {
            switch (actions[i]) {
                case 0:
                    nextRow[i] = Math.max(0, nextRow[i] - 1);
                    break; // N
                case 1:
                    nextRow[i] = Math.min(GRID_ROWS - 1, nextRow[i] + 1);
                    break; // S
                case 2:
                    nextCol[i] = Math.min(GRID_COLS - 1, nextCol[i] + 1);
                    break; // E
                case 3:
                    nextCol[i] = Math.max(0, nextCol[i] - 1);
                    break; // W
            }
        }
        int nextPos1 = nextRow[0] * GRID_COLS + nextCol[0];
        int nextPos2 = nextRow[1] * GRID_COLS + nextCol[1];

        double[] rewards = {-1.0, -1.0}; // Default step penalty for each agent
        boolean done = false;

        // --- 2. Check for the new "death" rule (Conflict Resolution) ---
        boolean agent1Dies = false;
        boolean agent2Dies = false;

        // If they try to move to the exact same resource square on the same turn, they both die.
        if (nextPos1 == nextPos2 && (nextPos1 == foodState1D || nextPos1 == waterState1D)) {
            agent1Dies = true;
            agent2Dies = true;
        }

        if (agent1Dies || agent2Dies) {
            if (agent1Dies) rewards[0] -= 100.0; // Strong penalty for dying
            if (agent2Dies) rewards[1] -= 100.0;
            done = true;
            // No need to update positions or check other rewards, the episode is over.
            return new MultiAgentStepResult(encodeState(), 0, rewards, true);
        }

        // --- 3. If no conflict, update positions ---
        agent1Row = nextRow[0];
        agent1Col = nextCol[0];
        agent2Row = nextRow[1];
        agent2Col = nextCol[1];

        // --- 4. Check for rewards and status changes for each agent ---
        // Agent 1
        if (nextPos1 == foodState1D && !agent1HasEaten) {
            agent1HasEaten = true;
            rewards[0] += 20.0;
        }
        if (nextPos1 == waterState1D && !agent1HasDrunk) {
            agent1HasDrunk = true;
            rewards[0] += 20.0;
        }
        if (nextPos1 == exitState1D) {
            rewards[0] += (agent1HasEaten && agent1HasDrunk) ? 50.0 : -50.0;
            done = true;
        }

        // Agent 2
        if (nextPos2 == foodState1D && !agent2HasEaten) {
            agent2HasEaten = true;
            rewards[1] += 20.0;
        }
        if (nextPos2 == waterState1D && !agent2HasDrunk) {
            agent2HasDrunk = true;
            rewards[1] += 20.0;
        }
        if (nextPos2 == exitState1D) {
            rewards[1] += (agent2HasEaten && agent2HasDrunk) ? 50.0 : -50.0;
            done = true;
        }

        int nextState = encodeState();
        return new MultiAgentStepResult(nextState, 0, rewards, done);
    }

    // Helper for visualization
    public Map<Integer, String> getSpecialStates() {
        final Map<Integer, String> special = new HashMap<>();
        special.put(startState1D, "S");
        special.put(exitState1D, "E");
        special.put(foodState1D, "F");
        special.put(waterState1D, "W");
        return special;
    }
}
