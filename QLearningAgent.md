# QLearningAgent Mechanics

The `QLearningAgent` implements the Q-Learning algorithm, a popular off-policy reinforcement learning algorithm. It learns an optimal policy by estimating the optimal action-value function (Q-function).

## Core Idea

Q-Learning aims to find the best action to take in any given state. It does this by learning a "Q-value" for each state-action pair, which represents the expected future reward for taking a specific action in a specific state and then following an optimal policy thereafter.

## Key Components

### 1. Q-Table

*   **Purpose:** The `qTable` (a 2D array) is the core of the Q-Learning agent. It stores the estimated Q-values for every possible state-action pair.
    *   `qTable[state][action]` represents the estimated value of taking `action` in `state`.
*   **Initialization:** Q-values are typically initialized to arbitrary small values (often zeros).

### 2. Parameters

The `QLearningAgent` is initialized with the following parameters, inherited from `BaseAgent`:

*   **`alpha` (Learning Rate):** (e.g., 0.5) Determines how much the newly acquired information overrides the old information. A value of 0 makes the agent learn nothing, while a value of 1 makes the agent consider only the most recent information.
*   **`gamma` (Discount Factor):** (e.g., 0.99) Determines the importance of future rewards. A value of 0 makes the agent "myopic" (only considers immediate rewards), while a value close to 1 makes it consider long-term rewards.
*   **`epsilon` (Exploration Rate):** (e.g., 0.1) Used in the epsilon-greedy strategy to balance exploration and exploitation.

### 3. Action Selection (`chooseAction` method - inherited from `BaseAgent`)

The agent uses an **epsilon-greedy strategy** to choose actions:

*   **Exploration (with probability `epsilon`):** The agent chooses a random action. This allows the agent to discover new, potentially better, paths.
*   **Exploitation (with probability `1 - epsilon`):** The agent chooses the action with the highest Q-value for the current state. This leverages the agent's current knowledge to maximize immediate reward.

### 4. Q-Value Update Rule (`update` method)

This is the most crucial part of the Q-Learning algorithm. The `update` method is called after each step in the environment and uses the following formula to update the Q-value of the previous state-action pair:

`Q(s, a) = Q(s, a) + alpha * [reward + gamma * max(Q(s', a')) - Q(s, a)]`

Where:
*   `Q(s, a)`: The current Q-value for the state `s` and action `a`.
*   `alpha`: The learning rate.
*   `reward`: The immediate reward received after taking action `a` in state `s` and transitioning to state `s'`.
*   `gamma`: The discount factor.
*   `max(Q(s', a'))`: The maximum Q-value for the *next state* `s'` across all possible actions `a'`. This is the key difference from SARSA; Q-Learning considers the *best possible future action* from the next state, regardless of what action the agent actually takes next (hence "off-policy").
*   `Q(s, a)`: The old Q-value for the state-action pair.

The `update` method in `QLearningAgent.java` calculates `tdTarget` (Temporal Difference Target) and `tdError` (Temporal Difference Error) to apply this update:

```java
// Find the maximum Q-value for the next state (ignores the actual next action)
double maxNextQ = Double.NEGATIVE_INFINITY;
for (int a = 0; a < numActions; a++) {
    if (qTable[nextState][a] > maxNextQ) {
        maxNextQ = qTable[nextState][a];
    }
}
final double tdTarget = reward + gamma * maxNextQ;
final double tdError = tdTarget - qTable[state][action];
qTable[state][action] += alpha * tdError;
```

## Off-Policy Learning

Q-Learning is an **off-policy** algorithm because its update rule uses the maximum Q-value of the next state (`max(Q(s', a'))`), which represents the value of the optimal policy, rather than the value of the action actually taken by the agent in the next step (which might be a random action due to exploration). This allows Q-Learning to learn the optimal policy even while the agent is exploring.
