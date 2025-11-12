package pt.mleiria.runner;

import pt.mleiria.rl.mdp.agent.Agent;
import pt.mleiria.rl.mdp.agent.QLearningAgent;
import pt.mleiria.rl.mdp.env.Environment;
import pt.mleiria.rl.mdp.env.FrozenLakeEnvironment;
import pt.mleiria.rl.mdp.utils.PrintUtils;
import pt.mleiria.rl.mdp.vo.AgentResult;
import pt.mleiria.rl.mdp.vo.StepResult;

import java.util.ArrayList;
import java.util.List;

public class QLearningRunnerFrozenLake {

    /**
     * Trains the given agent on the specified environment for a number of episodes.
     * This method is duplicated from ReinforcementLearningRunner for self-containment,
     * but in a larger project, it would ideally be refactored into a common utility.
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
                //PrintUtils.printGridPolicy(agent, env);
            }
            episodeRewards.add(totalReward);
            //PrintUtils.printGridPolicy(agent, env);
        }
        return new AgentResult(agent.getQTable(), episodeRewards);
    }

    public static void main(String[] args) {
        int episodes = 50000; // Increased episodes for better learning in FrozenLake

        System.out.println("=================================================");
        System.out.println("  RUNNING Q-LEARNING ON FROZEN LAKE ENVIRONMENT  ");
        System.out.println("=================================================");

        Environment frozenEnv = new FrozenLakeEnvironment();

        Agent qLearningAgentFrozen = new QLearningAgent(frozenEnv.getNumStates(), frozenEnv.getNumActions());

        System.out.println("Training Q-Learning Agent on Frozen Lake for " + episodes + " episodes...");
        AgentResult qResultFrozen = trainAgent(qLearningAgentFrozen, frozenEnv, episodes);

        System.out.println("\nTraining Complete. Displaying Results:");
        PrintUtils.printGridPolicy(qLearningAgentFrozen, frozenEnv);
        PrintUtils.printRewardSummary(qResultFrozen, qLearningAgentFrozen.getName());
    }
}
