package pt.mleiria.gametheory;

public class Game {
    private final Player player1;
    private final Player player2;
    private final int rounds;

    // The payoff matrix (points awarded)
    // You can change these values to see how it affects outcomes.
    private static final int BOTH_COOPERATE_SCORE = 3;
    private static final int BOTH_DEFECT_SCORE = 1;
    private static final int DEFECTOR_SCORE = 5; // You defect, they cooperate
    private static final int SUCKER_SCORE = 0;   // You cooperate, they defect

    public Game(Player player1, Player player2, int rounds) {
        this.player1 = player1;
        this.player2 = player2;
        this.rounds = rounds;
    }

    public void play() {
        System.out.printf("--- Starting Game: %s vs. %s for %d rounds ---\n\n",
                player1.getName(), player2.getName(), rounds);

        Choice p1LastChoice = null;
        Choice p2LastChoice = null;

        for (int i = 1; i <= rounds; i++) {
            Choice p1Choice = player1.makeChoice(p2LastChoice);
            Choice p2Choice = player2.makeChoice(p1LastChoice);

            // Determine scores for this round
            if (p1Choice == Choice.COOPERATE && p2Choice == Choice.COOPERATE) {
                player1.addToScore(BOTH_COOPERATE_SCORE);
                player2.addToScore(BOTH_COOPERATE_SCORE);
            } else if (p1Choice == Choice.DEFECT && p2Choice == Choice.COOPERATE) {
                player1.addToScore(DEFECTOR_SCORE);
                player2.addToScore(SUCKER_SCORE);
            } else if (p1Choice == Choice.COOPERATE && p2Choice == Choice.DEFECT) {
                player1.addToScore(SUCKER_SCORE);
                player2.addToScore(DEFECTOR_SCORE);
            } else { // Both defect
                player1.addToScore(BOTH_DEFECT_SCORE);
                player2.addToScore(BOTH_DEFECT_SCORE);
            }

            // Print round results
            System.out.printf("Round %d:\n", i);
            System.out.printf("  %s chose: %s\n", player1.getName(), p1Choice);
            System.out.printf("  %s chose: %s\n", player2.getName(), p2Choice);
            System.out.printf("  Scores -> %s: %d, %s: %d\n\n",
                    player1.getName(), player1.getScore(),
                    player2.getName(), player2.getScore());

            // Store choices for the next round's TitForTat logic
            p1LastChoice = p1Choice;
            p2LastChoice = p2Choice;
        }

        announceWinner();
    }

    private void announceWinner() {
        System.out.println("--- Final Results ---");
        System.out.printf("Final Score for %s: %d\n", player1.getName(), player1.getScore());
        System.out.printf("Final Score for %s: %d\n", player2.getName(), player2.getScore());

        if (player1.getScore() > player2.getScore()) {
            System.out.printf("Winner: %s!\n", player1.getName());
        } else if (player2.getScore() > player1.getScore()) {
            System.out.printf("Winner: %s!\n", player2.getName());
        } else {
            System.out.println("It's a tie!");
        }
        System.out.println("---------------------\n");
    }
}
