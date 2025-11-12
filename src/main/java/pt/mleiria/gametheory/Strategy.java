package pt.mleiria.gametheory;

/**
 * Represents a strategy for a player in the Prisoner's Dilemma game.
 */
public interface Strategy {

    /**
     * Make a choice based on the opponent's last choice.
     * @param opponentLastChoice The opponent's choice in the last round. Can be null if there is it is the first round.
     * @return the player's choice for the current round.
     */
    Choice makeChoice(final Choice opponentLastChoice);
}
