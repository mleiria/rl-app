# FrozenLakeEnvironment Mechanics

The `FrozenLakeEnvironment` is a classic reinforcement learning environment, often used as a benchmark for testing basic RL algorithms. It simulates an agent navigating a grid world where some tiles are frozen (safe to walk on) and others are holes (falling into them ends the episode). The goal is to reach a designated goal tile.

## Environment Setup

*   **Grid Size:** The environment is typically represented as a 4x4 grid, though the code allows for different sizes.
*   **Tiles:** Each tile can be one of four types:
    *   `S` (Start): The agent's starting position.
    *   `F` (Frozen): A safe frozen surface.
    *   `H` (Hole): A hole that ends the episode if the agent steps on it.
    *   `G` (Goal): The target destination that ends the episode successfully.

## State Space

*   **Representation:** The state space is discrete. Each unique tile in the grid corresponds to a unique state.
*   **Number of States:** For a 4x4 grid, there are 16 possible states (0 to 15). The state is simply the index of the agent's current position in the flattened grid.

## Action Space

*   **Actions:** The agent can take one of four discrete actions:
    *   `0`: Move Left
    *   `1`: Move Down
    *   `2`: Move Right
    *   `3`: Move Up
*   **Number of Actions:** There are 4 possible actions.

## Dynamics (Transitions)

*   **Stochasticity:** The `FrozenLakeEnvironment` is **stochastic**. This means that when the agent attempts to move in a certain direction, there's a chance it might move in an adjacent direction instead. For example, if the agent tries to move "Up", it might actually move "Left" or "Right" with a certain probability (e.g., 1/3 chance for intended direction, 1/3 for left, 1/3 for right). This makes the environment more challenging as the agent cannot perfectly control its movement.
*   **Boundary Conditions:** If an action would cause the agent to move off the grid, the agent remains in its current state.

## Rewards

*   **Goal:** Reaching the 'G' (Goal) tile yields a positive reward (e.g., +1.0).
*   **Hole:** Falling into an 'H' (Hole) tile yields a negative reward (e.g., 0.0, but the episode terminates, implying a penalty).
*   **Frozen/Start:** Moving to a 'F' (Frozen) or staying on 'S' (Start) tile typically yields a zero reward (0.0).
*   **Per Step:** There is usually no negative reward per step, encouraging the agent to find the goal efficiently.

## Termination Conditions

An episode terminates under two conditions:

1.  The agent reaches the 'G' (Goal) tile.
2.  The agent falls into an 'H' (Hole) tile.

## Resetting the Environment

The `reset()` method returns the agent to the 'S' (Start) tile, allowing for a new episode to begin.

## Challenges for RL Agents

The stochastic nature of the environment is the primary challenge. Agents must learn to account for the probabilistic outcomes of their actions to find an optimal policy that maximizes cumulative reward while avoiding holes.
