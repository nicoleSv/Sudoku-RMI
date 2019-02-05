package server;

import common.Board;
import common.LevelType;
import common.User;

import java.rmi.*;

/**
 * SudokuServer/server: SudokuServerInterface.java
 * Defines Server object interface
 */
public interface SudokuServerInterface extends Remote {
    /**
     * Generate a new game of Sudoku
     * @param level Difficulty level of the new game
     * @return Board object with valid Sudoku puzzle
     * @throws RemoteException
     */
    public Board generateSudoku(LevelType level) throws RemoteException;

    /**
     * Checks if board is containing a correct solution
     * @param board to be checked
     * @return true, if board has the correct solution
     * @throws RemoteException
     */
    public boolean isSolution(Board board) throws RemoteException;

    /**
     * Passes the correct solution of puzzle, in case user could not solve it
     * @return Board object with correct solution
     * @throws RemoteException
     */
    public Board showSolution() throws RemoteException;

    /**
     * Records statistics about a User in a file
     * @param user
     * @throws RemoteException
     */
    public void recordStatistics(User user) throws RemoteException;
}
