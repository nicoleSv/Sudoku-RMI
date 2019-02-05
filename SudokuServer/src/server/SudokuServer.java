package server;

import common.Board;
import common.LevelType;
import common.SudokuGame;
import common.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * SudokuServer/server: SudokuServer.java
 * Defines Server implementation object
 */
public class SudokuServer extends UnicastRemoteObject implements SudokuServerInterface {
    /**
     * SudokuServer has:
     *  - SudokuGame object, which implements the logic behind a game of Sudoku
     */
    private SudokuGame game;

    public SudokuServer() throws RemoteException {
        game = new SudokuGame();
    }

    /**
     * Generate a new game of Sudoku
     * @param level Difficulty level of the new game
     * @return Board object with valid Sudoku puzzle
     * @throws RemoteException
     */
    @Override
    public Board generateSudoku(LevelType level) {
        return game.generateGame(level);
    }

    /**
     * Checks if board is containing a correct solution
     * @param board to be checked
     * @return true, if board has the correct solution
     * @throws RemoteException
     */
    @Override
    public boolean isSolution(Board board) {
        Board correctBoard = game.getSolution();
        return correctBoard.equals(board);
    }

    /**
     * Passes the correct solution of puzzle, in case user could not solve it
     * @return Board object with correct solution
     * @throws RemoteException
     */
    @Override
    public Board showSolution() {
        return game.getSolution();
    }

    /**
     * Records statistics about a User in a file
     * @param user
     * @throws RemoteException
     */
    @Override
    public void recordStatistics(User user) {
        try {
            File statsFile = new File("statistics.txt");
            if(!statsFile.exists())
                statsFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(statsFile, true));
            writer.write(String.format("%s%n", user));
            System.out.println("Successfully written in file.");

            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in trying to record statistics.");
        }
    }
}
