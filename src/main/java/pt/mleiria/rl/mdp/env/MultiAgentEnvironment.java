package pt.mleiria.rl.mdp.env;

import pt.mleiria.rl.mdp.vo.MultiAgentStepResult;

public interface MultiAgentEnvironment {

    int getNumStates();
    int getNumActions();
    void reset();
    MultiAgentStepResult step(int action1, int action2);
}
