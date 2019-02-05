package common;

/**
 * SudokuServer/common: GameOutcome.java
 * Represents possible outcomes of Sudoku game
 */
public enum GameOutcome {
    SOLVED(1),
    UNSOLVED(0);

    private int gameResult;

    GameOutcome(int gameResult) {
        this.gameResult = gameResult;
    }

    public int getGameResult() {
        return gameResult;
    }

    @Override
    public String toString() {
        String result = "";

        switch(this.gameResult) {
            case 1:
                result = "solved";
                break;
            case 0:
                result = "unsolved";
                break;
        }

        return result;
    }
}
