package pt.mleiria.rl;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.agent.SARSAAgent;
import pt.mleiria.rl.mdp.env.CliffWalkingEnvironment;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.FrozenLakeEnvironment;
import pt.mleiria.rl.mdp.env.TaxiEnvironment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to run reinforcement learning agents on different environments.
 */
public class ReinforcementLearningRunner {


    public static void main(String[] args) {
        int episodes = 1000; // Number of episodes to train the agents
        //runCliffWalking(episodes);
        runFrozenLake(episodes);
        //runTaxi(episodes);
    }

    private static void runTaxi(int episodes) {
        System.out.println("\n=================================================");
        System.out.println("        RUNNING ON TAXI ENVIRONMENT              ");
        System.out.println("=================================================");
        Environment taxiEnv = new TaxiEnvironment();

        // Agents need to know the state and action space sizes of the new environment
        Agent qLearningAgentTaxi = new QLearningAgent(taxiEnv.getNumStates(), taxiEnv.getNumActions());
        Agent sarsaAgentTaxi = new SARSAAgent(taxiEnv.getNumStates(), taxiEnv.getNumActions());

        // Train agents (Taxi might need more episodes due to its larger state space)
        int taxiEpisodes = 5000;
        AgentResult qResultTaxi = trainAgent(qLearningAgentTaxi, taxiEnv, taxiEpisodes);
        AgentResult sarsaResultTaxi = trainAgent(sarsaAgentTaxi, taxiEnv, taxiEpisodes);

        // The generic PrintUtils.printGridPolicy won't be very meaningful for Taxi,
        // as the optimal action depends on passenger/destination state, not just location.
        // We will rely on the reward summary for evaluation.

        PrintUtils.printRewardSummary(qResultTaxi, qLearningAgentTaxi.getName());
        PrintUtils.printRewardSummary(sarsaResultTaxi, sarsaAgentTaxi.getName());
    }

    private static void runFrozenLake(int episodes) {
        // --- Run on FrozenLake Environment ---
        System.out.println("\n=================================================");
        System.out.println("      RUNNING ON FROZEN LAKE ENVIRONMENT         ");
        System.out.println("=================================================");
        Environment frozenEnv = new FrozenLakeEnvironment();

        Agent qLearningAgentFrozen = new QLearningAgent(frozenEnv.getNumStates(), frozenEnv.getNumActions());

        // Note: SARSA is less effective in sparse, zero-reward environments like FrozenLake,
        // as its conservatism can prevent it from finding the goal efficiently.
        // We will just demonstrate Q-Learning here.
        AgentResult qResultFrozen = trainAgent(qLearningAgentFrozen, frozenEnv, episodes * 500); // Needs more episodes

        PrintUtils.printGridPolicy(qLearningAgentFrozen, frozenEnv);
        PrintUtils.printRewardSummary(qResultFrozen, qLearningAgentFrozen.getName());
    }

    private static void runCliffWalking(int episodes) {
        // --- Run on CliffWalking Environment ---
        System.out.println("=================================================");
        System.out.println("    RUNNING ON CLIFF WALKING ENVIRONMENT         ");
        System.out.println("=================================================");
        Environment cliffEnv = new CliffWalkingEnvironment();

        Agent qLearningAgentCliff = new QLearningAgent(cliffEnv.getNumStates(), cliffEnv.getNumActions());
        Agent sarsaAgentCliff = new SARSAAgent(cliffEnv.getNumStates(), cliffEnv.getNumActions());

        AgentResult qResultCliff = trainAgent(qLearningAgentCliff, cliffEnv, episodes);
        AgentResult sarsaResultCliff = trainAgent(sarsaAgentCliff, cliffEnv, episodes);

        PrintUtils.printGridPolicy(qLearningAgentCliff, cliffEnv);
        PrintUtils.printGridPolicy(sarsaAgentCliff, cliffEnv);

        PrintUtils.printRewardSummary(qResultCliff, qLearningAgentCliff.getName());
        PrintUtils.printRewardSummary(sarsaResultCliff, sarsaAgentCliff.getName());
    }

    /**
     * Trains the given agent on the specified environment for a number of episodes.
     *
     * @param agent    The agent to be trained.
     * @param env      The environment in which the agent operates.
     * @param episodes The number of episodes to train the agent.
     * @return An AgentResult containing the Q-table and rewards from each episode.
     */
    public static AgentResult trainAgent(Agent agent, Environment env, int episodes) {
        List<Double> episodeRewards = new ArrayList<>();

        for (int episode = 0; episode < episodes; episode++) {
            int state = env.reset();
            boolean done = false;
            double totalReward = 0;

            // The on-policy/off-policy loop requires choosing the first action before the loop
            int action = agent.chooseAction(state);

            while (!done) {
                StepResult result = env.step(action);
                totalReward += result.reward();

                int nextAction = agent.chooseAction(result.nextState());

                agent.update(state, action, result.reward(), result.nextState(), nextAction);

                state = result.nextState();
                action = nextAction;
                done = result.done();
            }
            episodeRewards.add(totalReward);
        }
        return new AgentResult(agent.getQTable(), episodeRewards);
    }
}
