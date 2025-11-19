import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import java.util.Arrays;

public class Scratch {

    public static void main(String[] args) {
        final double[][][] transitionProbabilities = new double[3][3][3];
        transitionProbabilities[0] = new double[][]{
                {0.7, 0.3, 0.0},
                {1.0, 0.0, 0.0},
                {0.8, 0.2, 0.0}
        };
        transitionProbabilities[1] = new double[][]{
                {0.0, 1.0, 0.0},
                {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                {0.0, 0.0, 1.0}

        };
        transitionProbabilities[2] = new double[][]{
                {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                {0.8, 0.1, 0.1},
                {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}
        };
        final double[][][] rewards = new double[3][3][3];
        rewards[0] = new double[][]{
                {+10, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        rewards[1] = new double[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, -50}
        };
        rewards[2] = new double[][]{
                {0, 0, 0},
                {+40, 0, 0},
                {0, 0, 0}
        };

        final int[][] possibleActions = new int[][]{
                {0, 1, 2},
                {0, 2, -1},
                {1, -1, -1}
        };

        NDManager manager = NDManager.newBaseManager();
        // Flatten the 3D array into a 1D array ---
        int size = 3 * 3 * 3;
        double[] flattenedTransitionProbs = new double[size];
        double[] flattenedRewards = new double[size];
        double[] flattenedActions = new double[9];

        int index = 0;
        int index1 = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                flattenedActions[index1++] = possibleActions[i][j];
                for (int k = 0; k < 3; k++) {
                    flattenedTransitionProbs[index] = transitionProbabilities[i][j][k];
                    flattenedRewards[index] = rewards[i][j][k];
                    index++;
                }
            }
        }

        NDArray transitionProbabilitiesND = manager.create(flattenedTransitionProbs).reshape(3, 3, 3);
        NDArray rewardsND = manager.create(flattenedRewards).reshape(3, 3, 3);
        NDArray possibleActionsND = manager.create(flattenedActions).reshape(3, 3);

// Transition probability from s2 to s0 after playing action a1, we will look up
        System.out.println("Transition probability from s2 to s0 after playing action a1: " + transitionProbabilitiesND.get("2, 1, 0")); // 0.8
// The corresponding reward, we will look up rewards[2][1][0] (which is +40)
        System.out.println("The corresponding reward, we will look up rewards[2][1][0]: " + rewardsND.get("2, 1, 0")); // +40
// Possible actions in state s2
        System.out.println("Possible actions in state s2: " + possibleActionsND.get("2")); // [1]
    }
}
