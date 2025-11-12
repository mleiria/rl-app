package pt.mleiria.gametheory;

import java.util.Objects;

/**
 * Represents a strategy for a player in the Prisoner's Dilemma game that follows
 * This is the most famous and effective strategy
 */
public class TitForTat implements  Strategy {

    /**
     * Make a choice based on the opponent's last choice.
     * @param opponentLastChoice The opponent's choice in the last round. Can be null if there is it is the first round.
     * @return the player's choice for the current round, which is to mimic the opponent's last choice.
     */
    @Override
    public Choice makeChoice(final Choice opponentLastChoice) {
        return Objects.requireNonNullElse(opponentLastChoice, Choice.COOPERATE);
    }
}
