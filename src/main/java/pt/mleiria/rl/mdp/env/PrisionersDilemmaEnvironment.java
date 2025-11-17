package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.env.marl.MultiAgentEnvironment;
import pt.mleiria.rl.mdp.vo.MultiAgentStepResult;

public class PrisionersDilemmaEnvironment implements MultiAgentEnvironment {

    // Actions; 0 = COoperate, 1 = Defect
    public static final int COOPERATE = 0;
    public static final int DEFECT = 1;

    // States; 0 = Start, 1 = Opponent Cooperated, 2 = Opponent Defected
    public static final int STATE_START = 0;
    public static final int STATE_OPPONENT_COOPERATED = 1;
    public static final int STATE_OPPONENT_DEFECTED = 2;

    private final int roundsPerEpisode;
    private int currentRound;

    // Payoff Matrix: R(my_action, opponent_action) -> my_reward
    // R(C,C) = -1 (Temptation to defect)
    // R(C,D) = -10 (Sucker's payoff)
    // R(D,C) = 0 (Reward for defecting against a cooperator)
    // R(D,D) = -5 (Punishment for mutual defection)
    private static final double[][] PAYOFFS = {
            {-1, -10}, // My action COOPERATE
            {0, -5}    // My action DEFECT
    };

    public PrisionersDilemmaEnvironment(int roundsPerEpisode) {
        this.roundsPerEpisode = roundsPerEpisode;
        this.currentRound = 0;
    }


    @Override
    public int getNumStates() {
        return 3;
    }

    @Override
    public int getNumActions() {
        return 2;
    }

    @Override
    public void reset() {
        this.currentRound = 0;
    }

    @Override
    public MultiAgentStepResult step(int[] actions) {
        if (actions[0] < 0 || actions[0] > 1 || actions[1] < 0 || actions[1] > 1) {
            throw new IllegalArgumentException("Actions must be 0 (COOPERATE) or 1 (DEFECT)");
        }
        currentRound++;
        double[] rewards = new double[]{PAYOFFS[actions[0]][actions[1]], PAYOFFS[actions[1]][actions[0]]};
        //double reward1 = PAYOFFS[actions[0]][actions[1]];
        //double reward2 = PAYOFFS[actions[1]][actions[0]];

        int nextState1 = (actions[1] == COOPERATE) ? STATE_OPPONENT_COOPERATED : STATE_OPPONENT_DEFECTED;
        int nextState2 = (actions[0] == COOPERATE) ? STATE_OPPONENT_COOPERATED : STATE_OPPONENT_DEFECTED;

        boolean done = currentRound >= roundsPerEpisode;

        return new MultiAgentStepResult(nextState1, nextState2, rewards, done);
    }
}