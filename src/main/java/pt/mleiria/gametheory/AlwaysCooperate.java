package pt.mleiria.gametheory;

/**
 * Represents a strategy for a player in the Prisoner's Dilemma game that always cooperates.
 * The player is a saint, always choosing to cooperate regardless of the opponent's last choice.
 */
public class AlwaysCooperate implements  Strategy {

    /**
     * Make a choice based on the opponent's last choice.
     * @param opponentLastChoice The opponent's choice in the last round. Can be null if there is it is the first round.
     * @return the player's choice for the current round, which is always to cooperate.
     */
    @Override
    public Choice makeChoice(final Choice opponentLastChoice) {
        return Choice.COOPERATE;
    }
}
