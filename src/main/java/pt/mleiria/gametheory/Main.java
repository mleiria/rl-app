package pt.mleiria.gametheory;

public class Main {
    public static void main(String[] args) {
        // --- Matchup 1: Tit for Tat vs. Always Defect ---
        // Tit for Tat gets tricked once, then defects for the rest of the game.
        // Always Defect wins, but not by a huge margin.
        Player playerA_TFT = new Player("Tit for Tat", new TitForTat());
        Player playerB_Defector = new Player("Always Defect", new AlwaysDefect());
        Game game1 = new Game(playerA_TFT, playerB_Defector, 10);
        game1.play();

        // --- Matchup 2: Tit for Tat vs. Always Cooperate ---
        // Tit for Tat cooperates the whole time, leading to a high score for both.
        // The defector would have exploited the cooperator and won.
        Player playerC_TFT = new Player("Tit for Tat", new TitForTat());
        Player playerD_Cooperator = new Player("Always Cooperate", new AlwaysCooperate());
        Game game2 = new Game(playerC_TFT, playerD_Cooperator, 10);
        game2.play();

        // --- Matchup 3: Tit for Tat vs. Tit for Tat ---
        // The most stable and high-scoring pair. They cooperate the entire time.
        // This shows how cooperation can emerge between rational "nice" players.
        Player playerE_TFT1 = new Player("Tit for Tat #1", new TitForTat());
        Player playerF_TFT2 = new Player("Tit for Tat #2", new TitForTat());
        Game game3 = new Game(playerE_TFT1, playerF_TFT2, 10);
        game3.play();
    }
}
