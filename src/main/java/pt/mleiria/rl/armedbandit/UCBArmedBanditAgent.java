package pt.mleiria.rl.armedbandit;

/**
 * An agent that follows the Upper Confidence Bound (UCB) strategy.
 */
class UCBArmedBanditAgent implements ArmedBanditAgent {
    private final double c;
    private final int numArms;
    private int totalPulls;
    private final int[] armPullCounts;
    private final double[] estimatedValues;

    public UCBArmedBanditAgent(int numArms, double c) {
        this.numArms = numArms;
        this.c = c;
        this.totalPulls = 0;
        this.armPullCounts = new int[numArms];
        this.estimatedValues = new double[numArms];
    }

    @Override
    public int chooseArm() {
        this.totalPulls++;

        // First, play each arm once to avoid division by zero
        for (int arm = 0; arm < this.numArms; arm++) {
            if (this.armPullCounts[arm] == 0) {
                return arm;
            }
        }

        // Calculate UCB values for each arm
        int bestArm = 0;
        double maxUcbValue = Double.NEGATIVE_INFINITY;
        for (int arm = 0; arm < this.numArms; arm++) {
            double exploitationTerm = this.estimatedValues[arm];
            double explorationTerm = this.c * Math.sqrt(Math.log(this.totalPulls) / this.armPullCounts[arm]);
            double ucbValue = exploitationTerm + explorationTerm;

            if (ucbValue > maxUcbValue) {
                maxUcbValue = ucbValue;
                bestArm = arm;
            }
        }
        return bestArm;
    }

    @Override
    public void update(int armIndex, int reward) {
        this.armPullCounts[armIndex]++;
        int n = this.armPullCounts[armIndex];
        double oldValue = this.estimatedValues[armIndex];
        this.estimatedValues[armIndex] = oldValue + (1.0 / n) * (reward - oldValue);
    }

    @Override
    public double[] getEstimatedValues() {
        return this.estimatedValues;
    }

    @Override
    public int[] getArmPullCounts() {
        return this.armPullCounts;
    }
}
