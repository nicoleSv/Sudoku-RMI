package common;

import java.util.*;

/**
 * SudokuServer/common: SudokuGame.java
 * Generates sudoku games
 */
public class SudokuGame {
    /**
     * Sudoku Game has:
     *  - solver, which helps generate sudoku, and checks user's board for correct solution
     *  - board
     *  - blankCells - list of cells with value of 0
     *  - limitBlankCells - number of blanks cells, depending on the difficulty level
     */
    private Solver solver;
    private Board board;
    private List<Cell> blankCells;
    private int limitBlankCells;

    public SudokuGame() {
        solver = new Solver();
        board = new Board();
    }

    /**
     * Generates new Sudoku puzzle, using Las Vegas algorithm and Backtracking
     * @param level difficulty level for puzzle
     * @return Board object with valid Sudoku puzzle
     */
    public Board generateGame(LevelType level) {
        board = new Board();

        // using Las Vegas algorithm to yield a puzzle with 11 random given cell's values
        lasVegas();

        // generates full Sudoku board, using Solver to solve the grid yield by lasVegas()
        generateFullBoard();
        saveSolution();

        // digs a bunch of random holes, so that they can be spread evenly
        randomizeHoles(level);

        // get number of blank cells for current board and dig same number of holes in board
        limitBlankCells = level.getBlankCells();
        board.setOrder(level.getSequenceType());
        digHoles();
        board.saveCellValues();

        return board;
    }

    /**
     * Generates full Sudoku board, using Solver to fill in the blank cells
     */
    private void generateFullBoard() {
        solver.setBoard(board);
        solver.solve(0, 0);
    }

    /**
     * If level difficulty is medium or hard, dig 30 holes as a start, so that blank cell are spread evenly
     * @param level
     */
    private void randomizeHoles(LevelType level) {
        if(level.equals(LevelType.MEDIUM) || level.equals(LevelType.HARD)) {
            limitBlankCells = 30;
            board.setOrder(Sequence.RANDOM);

            digHoles();
        }
    }

    /**
     * Dig holes (blank cells) in full Sudoku board. The result must be a valid Sudoku puzzle with unique solution
     */
    private void digHoles() {
        ListIterator<Cell> iterator = board.iterator();
        board.saveCellValues();

        while(iterator.hasNext()) {
            Cell currentCell = iterator.next();
            currentCell.saveValue();

            if(currentCell.isBlank()) continue;
            else currentCell.setValue(0);

            solver.setBoard(board);


            if(isUnique())
                currentCell.saveValue();
            else
                board.loadSavedValues();

            blankCells = getBlankCells(board);

            if(isOutOfLimits()) break;
            }
    }

    /**
     * Saves the solution for Sudoku puzzle, before it has holes
     */
    private void saveSolution() {
        for(Cell cell : board.getCells())
            cell.setCorrectValue();
    }

    /**
     * Determines if current Sudoku puzzle is unique. Sudoku puzzle is considered unique if has only one solution
     * @return true, if Solver found only one solution
     */
    private boolean isUnique() {
        return solver.countSolutions(0,0) == 1;
    }

    /**
     * @return true, if number of bank cells is reached already
     */
    private boolean isOutOfLimits() {
        if(blankCells != null)
            return blankCells.size() >= limitBlankCells;
        else return false;
    }

    /**
     * @param board
     * @return List of cells with value of 0 (blank cells)
     */
    private static List<Cell> getBlankCells(Board board) {
        List<Cell> blanks = new ArrayList<>();
        for(Cell cell : board.getCells()) {
            if(cell.isBlank()) {
                blanks.add(cell);
            }
        }
        return blanks;
    }

    /**
     * Using Las Vegas algorithm to fill in empty Sudoku grid with 11 values.
     * The rest cells are filled in according to these givens later.
     */
    private void lasVegas() {
        int givens = 11;
        board.createGrid();
        Random random = new Random();

        // generate 11 random givens
        for(int i = 0; i < givens; i++) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            int randomValue;
            do {
                randomValue = 1 + random.nextInt(9);
            } while(!board.isValid(board.getCell(row, col), randomValue));

            board.getCell(row,col).setValue(randomValue);
        }
    }

    /**
     * @return Board object with correct solution for Sudoku puzzle
     */
    public Board getSolution() {
        return board.getSolution();
    }
}

