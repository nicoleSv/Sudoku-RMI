package common;

import java.util.Random;

/**
 * SudokuServer/common: LevelType.java
 * Represents difficulty levels in Sudoku game
 */
public enum LevelType {
    EASY(39, 45, Sequence.RANDOM),
    MEDIUM(46, 49, Sequence.LINEAR),
    HARD(50, 53, Sequence.S_FORM);

    private int minBlankCells;
    private int maxBlankCells;
    private Sequence sequenceType;

    private LevelType(int min, int max, Sequence sequence) {
        this.minBlankCells = min;
        this.maxBlankCells = max;
        this.sequenceType = sequence;
    }

    /**
     * Generates a number of blank cells in sudoku. Range is defined by the difficulty level
     * @return number of blank cells in sudoku grid
     */
    public int getBlankCells() {
        return new Random()
                .nextInt((maxBlankCells - minBlankCells) + 1)
                + minBlankCells;
    }

    /**
     * @return Sequence enum which defines how Sudoku cells to be digged depending on the level
     */
    public Sequence getSequenceType() {
        return sequenceType;
    }

    @Override
    public String toString() {
        String levelType = "";

        switch(this) {
            case EASY:
                levelType = "easy";
                break;
            case MEDIUM:
                levelType = "medium";
                break;
            case HARD:
                levelType = "hard";
                break;
        }

        return levelType;
    }
}
