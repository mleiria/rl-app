package pt.mleiria.gametheory;

public class AlwaysDefect implements Strategy {

    /**
     * Make a choice based on the opponent's last choice.
     * @param opponentLastChoice The opponent's choice in the last round. Can be null if there is it is the first round.
     * @return the player's choice for the current round, which is always to defect.
     */
    @Override
    public Choice makeChoice(final Choice opponentLastChoice) {
        return Choice.DEFECT;
    }
}
