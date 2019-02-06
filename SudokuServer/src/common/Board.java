package common;

import java.io.Serializable;
import java.util.*;

/**
 *  SudokuServer/common: Board.java
 *  Represents a board for solving Sudoku
 */
public class Board implements Iterable<Cell>, Serializable {
    /**
     * Board has:
     *  - 2d grid of cells (9x9)
     *  - 2d list of blocks, each block is (3x3)
     *  - list with all cells
     *  - order in which cells are shuffled
     */
    private Cell[][] grid;
    private List<List<Cell>> blocks;
    private List<Cell> cells;
    private Sequence order;

    public Board() {
        grid = new Cell[9][9];
        createBlocks();
        cells = new ArrayList<>(9*9);
    }

    private void createBlocks() {
        blocks = new ArrayList<>(9);
        for(int i = 0; i < 9; i++)
            blocks.add(new ArrayList<>(9));
    }

    public List<List<Cell>> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public Cell[][] getGrid() { return grid; }

    /**
     * Returns a Cell object
     * @param row
     * @param col
     * @return cell which is in given row and column
     */
    public Cell getCell(int row, int col) {
        if(row < 0 || row > 8 || col < 0 || col > 8)
            return null;

        return grid[row][col];
    }

    public List<Cell> getCells() {
        return new ArrayList<>(cells);
    }

    void setOrder(Sequence order) {
        this.order = (order != null) ? order : Sequence.LINEAR;
    }

    public Sequence getOrder() {
        return order;
    }

    /**
     * Creates an empty grid, all cell's values are 0;
     * Saves cell objects in List cells and List<List> blocks for future use
     */
    public void createGrid() {
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                Cell cell = new Cell(row, col);
                grid[row][col] = cell;
                cells.add(cell);
                blocks.get(cell.getBlock()).add(cell);
            }
        }
    }

    /**
     * Checks if value could be placed in given cell
     * @param cell Cell in which is tried to place value
     * @param value Value to be placed in cell
     * @return if value is valid to place in cell
     */
    public boolean isValid(Cell cell, int value) {
        if(cell == null) return false;

        return testRow(cell.getRow(), value) &&
                testColumn(cell.getColumn(), value) &&
                testBlock(cell, value);
    }

    /**
     * Tests if value is valid for given row according to Sudoku rules
     * @param row
     * @param value
     * @return true, if value is not already in given row; false, if it is
     */
    private boolean testRow(int row, int value) {
        if(row < 0 || row > 8 || value < 1 || value > 9)
            return false;

        for(Cell cell : grid[row]) {
            if(cell.getValue() == value)
                return false;
        }

        return true;
    }

    /**
     * Tests if value is valid for given column according to Sudoku rules
     * @param col
     * @param value
     * @return true, if value is not already in given column; false, if it is
     */
    private boolean testColumn(int col, int value) {
        if(col < 0 || col > 8 || value < 1 || value > 9)
            return false;

        for(Cell[] cells : grid) {
            if(cells[col].getValue() == value)
                return false;
        }

        return true;
    }

    /**
     * Tests if value is valid for block of given cell according to Sudoku rules
     * @param testedCell
     * @param value
     * @return true, if value is not already in current block; false, if it is
     */
    private boolean testBlock(Cell testedCell, int value) {
        if(testedCell == null || !(value > 0 && value <= 9))
            return false;

        for(Cell cell : blocks.get(testedCell.getBlock())) {
            if(cell.getValue() == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Saves all cell's current values for future use
     */
    void saveCellValues() {
        cells.forEach(Cell::saveValue);
    }

    /**
     * Loads all cell's saved values
     */
    void loadSavedValues() {
        cells.forEach(Cell::loadSavedValue);
    }

    /**
     * Shuffles cells list, so that it cells can be digged randomly
     * @return ListIterator for cells list
     */
    private ListIterator<Cell> getRandomOrderIterator() {
        List<Cell> randomOrder = new ArrayList<>(cells);
        Collections.shuffle(randomOrder);

        return randomOrder.listIterator();
    }

    /**
     * @return ListIterator for cells list which iterates cells in linear order
     */
    private ListIterator<Cell> getLinearOrderIterator() {
        return cells.listIterator();
    }

    /**
     * Constructs a list from the grid wandering it along "S", so that cells can be digged in S-form
     * @return ListIterator for cells list
     */
    private ListIterator<Cell> getSFormOrderIterator() {
        List<Cell> sForm = new ArrayList<>();
        List<Cell> temp;

        for(int row = 0; row < 9; row++) {
            if((row+1)%2 == 0) {
                temp = new ArrayList<>(Arrays.asList(grid[row]));
                Collections.reverse(temp);
                sForm.addAll(temp);
            }
            else
                sForm.addAll(Arrays.asList(grid[row]));
        }

        return sForm.listIterator();
    }

    /**
     * @return ListIterator of Board object with set order for iteration
     */
    @Override
    public ListIterator<Cell> iterator() {
        switch(order) {
            case RANDOM: return getRandomOrderIterator();
            case LINEAR: return getLinearOrderIterator();
            case S_FORM: return getSFormOrderIterator();
            default: return getRandomOrderIterator();
        }
    }

    /**
     * @return String displaying the Sudoku board
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(int row = 0; row < 9; row++) {
            builder.append("\n");
            if(row % 3 == 0) builder.append("\n");

            for(int col = 0; col < 9; col++) {
                if(col % 3 == 0) builder.append(" ");
                int value = grid[row][col].getValue();
                builder.append(value == 0 ? "." : value);
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        if(!(obj instanceof Board))
            return false;

        Board otherBoard = (Board) obj;
        return cells.equals(otherBoard.getCells());
    }

    Board getSolution() {
        Board correctBoard = new Board();
        correctBoard.createGrid();

        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                int currentValue = grid[row][col].getCorrectValue();
                correctBoard.getCell(row, col).setValue(currentValue);
            }
        }

        return correctBoard;
    }
}
