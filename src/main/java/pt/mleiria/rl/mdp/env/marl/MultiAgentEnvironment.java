package pt.mleiria.rl.mdp.env.marl;

import pt.mleiria.rl.mdp.vo.MultiAgentStepResult;

public interface MultiAgentEnvironment {

    int getNumStates();
    int getNumActions();
    void reset();
    MultiAgentStepResult step(int[] action);
}
