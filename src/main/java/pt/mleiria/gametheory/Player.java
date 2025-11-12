package pt.mleiria.gametheory;

public class Player {

    private final String name;
    private final Strategy strategy;
    private int score;

    /**
     * Constructor for Player.
     *
     * @param name     the name of the player
     * @param strategy the strategy that the player will use to make choices
     */
    public Player(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
        this.score = 0;
    }

    /**
     * Make a choice based on the opponent's last choice.
     *
     * @param opponentLastChoice The opponent's choice in the last round. Can be null if it is the first round.
     * @return the player's choice for the current round.
     */
    public Choice makeChoice(final Choice opponentLastChoice) {
        return strategy.makeChoice(opponentLastChoice);
    }

    /**
     * Add points to the player's score.
     *
     * @param points the number of points to add to the player's score
     */
    public void addToScore(int points) {
        this.score += points;
    }

    /**
     * Get the player's name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Get the player's strategy.
     *
     * @return the strategy that the player is using
     */
    public int getScore() {
        return score;
    }
}
