package usergui;

import common.Cell;

import java.util.Stack;

/**
 * SudokuClient/usergui: UndoRedoManager.java
 * Implements undo/redo functionality for Sudoku game
 */
public class UndoRedoManager {
    /**
     * UndoRedoManager has:
     *  - undoStack, which holds already made moves, available for undoing
     *  - redoStack, which holds moves, available for redoing
     */
    private Stack<Cell> undoStack;
    private Stack<Cell> redoStack;

    public UndoRedoManager() {
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    /**
     * Adds a new made move to undoStack; clears redoStack as the moves there become unreachable
     * @param currentCell
     */
    public void addMove(Cell currentCell) {
        undoStack.push(currentCell);
        redoStack.clear();
    }

    /**
     * Pops the last made move (last changed cell) from the undoStack and pushes it to redoStack for future use
     * @return Cell object, which is the last changed cell
     */
    public Cell undoMove() {
        if(undoStack.isEmpty())
            return null;


        Cell cell = undoStack.pop();
        redoStack.push(cell);

        return new Cell(cell);
    }

    /**
     * Pops from redoStack and pushes into undoStack again for future use
     * @return Cell object to redone
     */
    public Cell redoMove() {
        if(redoStack.isEmpty())
            return null;

        Cell cell = redoStack.pop();
        undoStack.push(cell);

        return new Cell(cell);
    }

}
