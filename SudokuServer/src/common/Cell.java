package common;

import java.io.Serializable;

/**
 * SudokuServer/common: Cell.java
 * Represents a cell in Sudoku grid
 */
public class Cell implements Serializable {
    /**
     * Cell has:
     *  - row, column, block - defining the position of cell
     *  - value - current value of cell
     *  - savedValue, correctValue, previousValue - variables helping for implementing the logic
     */
    final private int row;
    final private int column;
    final private int block;
    private int value;
    private int savedValue;
    private int correctValue;
    private int previousValue;

    public Cell(int row, int column) {
        this.row = row > 0 && row < 9 ? row : 0;
        this.column = column > 0 && column < 9 ? column : 0;
        block = calculateBlock();
        setValue(0);
    }

    public Cell(Cell otherCell) {
        this.row = otherCell.row > 0 && otherCell.row < 9 ? otherCell.row : 0;
        this.column = otherCell.column > 0 && otherCell.column < 9 ? otherCell.column : 0;
        this.block = otherCell.block > 0 && otherCell.block < 9 ? otherCell.block : 0;
        this.value = otherCell.value >= 1 && otherCell.value <= 9 ? otherCell.value : 0;
        this.savedValue = otherCell.savedValue >= 1 && otherCell.savedValue <= 9 ? otherCell.savedValue : 0;
        this.correctValue = otherCell.correctValue >= 1 && otherCell.correctValue <= 9 ? otherCell.correctValue : 0;
        this.previousValue = otherCell.previousValue >= 1 && otherCell.previousValue <= 9 ? otherCell.previousValue : 0;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getBlock() {
        return block;
    }

    public int getValue() {
        return value;
    }

    /**
     * Sets new value for a cell and saves current for future use
     * @param value New value to be set
     */
    public void setValue(int value) {
        this.previousValue = this.value;
        this.value = value >= 1 && value <= 9 ? value : 0;
    }

    public int getPreviousValue() { return previousValue; }

    int getCorrectValue() {
        return correctValue;
    }

    void setCorrectValue() {
        this.correctValue = value;
    }

    /**
     * Cell is treated like blank if it's value is zero
     * @return true, if value is 0; false, if it is not
     */
    public boolean isBlank() {
        return value == 0;
    }

    /**
     *
     * @return block to which the Cell belongs (integer in range 0-8)
     */
    int calculateBlock() {
        return (row / 3) * 3 + column / 3; // + 1 if we want 1-9
    }

    /**
     * Saves current value of cell for future use
     */
    void saveValue() {
        savedValue = value;
    }

    /**
     * Loads the saved value
     */
    void loadSavedValue() {
        value = savedValue;
    }


    /**
     * @return String displaying the current value of cell
     */
    @Override
    public String toString() {
        return String.format("%d", value);
    }

    /**
     * @param obj
     * @return true, if two cells have equal values, rows and columns
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Cell)) {
            return false;
        }
        Cell cell = (Cell) obj;
        return cell.getValue() == this.getValue() &&
                cell.getRow() == this.getRow() &&
                cell.getColumn() == this.getColumn();
    }
}
