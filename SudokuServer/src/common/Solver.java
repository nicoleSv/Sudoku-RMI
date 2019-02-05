package common;

/**
 * SudokuServer/common: Solver.java
 * Solving sudoku boards
 */
public class Solver {
    /**
     * Solver has:
     *  - board, which has to be solved
     *  - count, which holds number of solutions
     */
    private Board board;
    private int count = 0;

    void setBoard(Board board){
        if(board != null)
            this.board = board;

        count = 0;
    }

    /**
     * Solves a sudoku board, starting from given row and column, and counts the solutions. Uses backtracking algorithm.
     * It helps in defining if a board is unique, so it only needs to find 2 solutions top to know if it is not.
     * @param row
     * @param col
     * @return 0, 1 or 2, depending on how much solutions it found
     */
    int countSolutions(int row, int col) {
        if(row == 9) {
            // row 9 does not exist, overflow back to 0
            row = 0;
            if(++col == 9)
                // column 9 does not exist, so end of grid is reached
                // which means that 1 more solution is found
                return count + 1;
        }

        // skip cells which already have values
        if(!board.getCell(row, col).isBlank())
            return countSolutions(row + 1, col);

        // filling values until they are valid
        for(int value = 1; value <= 9 && count < 2; ++value) {
            if(board.isValid(board.getCell(row, col), value)) {
                board.getCell(row, col).setValue(value);

                // add additional solutions
                count = countSolutions(row + 1, col);
            }
        }

        // solution failed, so backtracking...
        board.getCell(row, col).setValue(0);
        return count;
    }

    /**
     * Only solves a sudoku board using backtracking algorithm. Helps in generating full sudoku board.
     * @param row
     * @param col
     * @return true, if sudoku can be solved
     */
    boolean solve(int row, int col) {
        if(row == 9) {
            row = 0;
            if(++col == 9)
                return true;
        }

        if(!board.getCell(row, col).isBlank())
            return solve(row + 1, col);

        for(int value = 1; value <= 9; ++value) {
            if(board.isValid(board.getCell(row, col), value)) {
                board.getCell(row, col).setValue(value);

               if(solve(row + 1, col))
                   return true;
            }
        }

        board.getCell(row, col).setValue(0);
        return false;
    }
}

