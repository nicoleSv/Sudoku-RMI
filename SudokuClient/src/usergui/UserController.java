package usergui;

import common.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import server.*;
import common.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Optional;

/**
 * SudokuClient/usergui: UserController.java
 * Controls the user GUI
 */
public class UserController {

    /**
     *  Visual parts of the GUI
     */
    @FXML private Button btnOne;
    @FXML private Button btnTwo;
    @FXML private Button btnThree;
    @FXML private Button btnFour;
    @FXML private Button btnFive;
    @FXML private Button btnSix;
    @FXML private Button btnSeven;
    @FXML private Button btnEight;
    @FXML private Button btnNine;
    @FXML private Canvas canvas;
    @FXML private Button btnCheckInput;
    @FXML private Button btnNewGame;
    @FXML private Button btnShowSolution;
    @FXML private Button btnExit;
    @FXML private TitledPane tpLevel;
    @FXML private ToggleGroup tglGrpLevels;
    @FXML private RadioButton optEasy;
    @FXML private RadioButton optMedium;
    @FXML private RadioButton optHard;
    @FXML private Button btnRedo;
    @FXML private Button btnUndo;
    @FXML private Label lblTimer;

    // Holds current selected row
    private int selectedRow;
    // Holds current selected column
    private int selectedColumn;
    // Reference to the server interface
    private SudokuServerInterface sudokuServer;
    // Holds initial board with puzzle to solve
    private Board initialBoard;
    // Holds user board with moves made by user
    private Board userBoard;
    // Holds information about user
    private User user;
    // Reference to UndoRedoManager object which implements undo/redo functionality
    private UndoRedoManager movesManager;
    // Holds current level of difficulty played
    private LevelType currentLevel;
    // Boolean to define whether current puzzle is solved
    private boolean isSolved;
    // Reference to TimerManager object which implements timer functionality
    private TimerManager timer;

    @FXML
    public void initialize() {
        initializeRMI();

        timer = new TimerManager(lblTimer);
        user = new User(SudokuUser.getUsername());
        currentLevel = LevelType.EASY;
        startNewGame();
    }

    /**
     * Initializes RMI; connects to the server
     */
    private void initializeRMI() {
        String host = "localhost";
        try {
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            sudokuServer = (SudokuServerInterface) registry.lookup("sudoku");
            System.out.println("Server " + sudokuServer + " is found.");
        } catch (Exception e) {
            System.out.println("ERROR " + e);
            quitApp();
        }
    }

    /**
     * Requests new game from the server;
     * Saves information about the user;
     * Starts timer
     */
    private void startNewGame() {
        try {
            initialBoard = sudokuServer.generateSudoku(currentLevel); // default
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        user.addDifficultyLevel(currentLevel);
        userBoard = new Board();
        userBoard.createGrid();

        movesManager = new UndoRedoManager();
        isSolved = false;

        selectedRow = 0;
        selectedColumn = 0;

        GraphicsContext context = canvas.getGraphicsContext2D();
        drawOnCanvas(context);

        timer.startTimer();
    }

    /**
     * Stops timer and saves information about the outcome of the game
     * @param outcome
     */
    private void saveResults(GameOutcome outcome) {
        timer.stopTimer();
        user.addTimePlayed(timer.getMilliseconds());
        user.addGameResult(outcome);
    }

    /**
     * Adds a new cell's value to user board
     * @param value
     * @param row
     * @param col
     */
    private void modifyUserBoard(int value, int row, int col) {
        // Checks if the initialBoard has a zero (treated as blank cell) in the position put in a number in userBoard
        // This way are avoided intersections between the two
        if (initialBoard.getCell(row, col).isBlank()) {
            if(value >=0 && value <= 9)
                userBoard.getCell(row, col).setValue(value);
            else
                // print out an error message
                System.out.println("The passed value is not valid.");
        }
        else
            System.out.println("This cell should not be changed!");
    }

    /**
     * Draws Sudoku board and Cells' values in canvas
     * @param context
     */
    private void drawOnCanvas(GraphicsContext context) {
        // draw sudoku grid
        drawRectangles(context);
        // draw the initial numbers
        drawInitialValues(context);
        // draw the user's numbers
        drawUserValues(context);
    }

    /**
     * Draws 9x9 white rectangles, which represent blank cells
     */
    private void drawRectangles(GraphicsContext context) {
        context.clearRect(0, 0, 450, 450);
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                // finds the y position of the cell, by multiplying the row number by 50, which is the height of a row
                // then adds 2, to add some offset
                int positionY = row * 50 + 2;
                int positionX = col * 50 + 2;

                // defines the width of the square as 46 instead of 50, to account for the 4px total of blank space
                int width = 46;
                context.setFill(Color.WHITE);

                // draw a rounded rectangle with the calculated position and width
                context.fillRoundRect(positionX, positionY, width, width, 10, 10);
            }
        }

        // draw black lines between the blocks of sudoku grid
        context.setStroke(Color.BLACK);
        context.setLineWidth(4);
        context.strokeLine(0, 0, 0, 450);
        context.strokeLine(450, 0, 450, 450);
        context.strokeLine(0, 0, 450, 0);
        context.strokeLine(0, 450, 450, 450);

        context.strokeLine(150, 0, 150, 450);
        context.strokeLine(300, 0, 300, 450);
        context.strokeLine(0, 150, 450, 150);
        context.strokeLine(0, 300, 450, 300);

        // draw highlight around selected cell
        context.setStroke(Color.RED);
        context.setLineWidth(4);
        context.strokeRoundRect(selectedColumn * 50 + 2, selectedRow * 50 + 2, 46, 46, 10, 10);
    }

    /**
     * Draws the values of sudoku puzzle, which is generated by the server
     * @param context
     */
    private void drawInitialValues(GraphicsContext context) {
        Cell[][] initialCells = initialBoard.getGrid();
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                int positionY = row * 50 + 32;
                int positionX = col * 50 + 20;

                context.setFill(Color.BLACK);
                context.setFont(new Font(24));

                if(!initialCells[row][col].isBlank())
                    context.fillText(initialCells[row][col].toString(), positionX, positionY);
            }
        }
    }

    /**
     * Draws the values, which a user inputs. They must not intersect with those of initialBoard
     * @param context
     */
    private void drawUserValues(GraphicsContext context) {
        Cell[][] userCells = userBoard.getGrid();
        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                int positionY = row * 50 + 30;
                int positionX = col * 50 + 20;

                context.setFill(Color.DEEPSKYBLUE);
                context.setFont(new Font(20));
                // check if value of corresponding cell is not 0 and that cell is not in initialBoard
                if(!userCells[row][col].isBlank() && initialBoard.getCell(row, col).isBlank())
                    context.fillText(userCells[row][col].toString(), positionX, positionY);
            }
        }
    }

    /**
     * Determines in which cell the user has clicked
     * @param event
     */
    @FXML
    void canvasMouseClicked(MouseEvent event) {
        // intercept the mouse position relative to the canvas and cast it to an integer
        int mouseX = (int) event.getX();
        int mouseY = (int) event.getY();

        // convert the mouseX and mouseY into rows and cols
        // This way any value between 0 and 449 for x and y is going to give an integer from 0 to 8
        selectedRow = mouseY / 50; // update player selected row
        selectedColumn = mouseX / 50; // update player selected column

        //get the canvas graphics context and redraw
        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    /**
     * On clicking a number button, the corresponding value is added to user board;
     * the move is saved in UndoRedoManager for future use; and the changes are drawn again on the canvas
     * @param event
     */
    @FXML
    void btnOneClicked(MouseEvent event) {
        modifyUserBoard(1, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnTwoClicked(MouseEvent event) {
        modifyUserBoard(2, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnThreeClicked(MouseEvent event) {
        modifyUserBoard(3, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnFourClicked(MouseEvent event) {
        modifyUserBoard(4, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnFiveClicked(MouseEvent event) {
        modifyUserBoard(5, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnSixClicked(MouseEvent event) {
        modifyUserBoard(6, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnSevenClicked(MouseEvent event) {
        modifyUserBoard(7, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnEightClicked(MouseEvent event) {
        modifyUserBoard(8, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    void btnNineClicked(MouseEvent event) {
        modifyUserBoard(9, selectedRow, selectedColumn);

        Cell currentCell = new Cell(userBoard.getCell(selectedRow, selectedColumn));
        movesManager.addMove(currentCell);

        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    /**
     * On clicking the Undone button, UndoRedoManager returns the last changed cell, if there is one;
     * The cell's previous value is displayed
     * @param event
     */
    @FXML
    void btnUndoClicked(MouseEvent event) {
        Cell undoneCell = movesManager.undoMove();
        if(undoneCell == null) {
            System.out.println("No move to undone");
            return;
        }
        else {
            modifyUserBoard(undoneCell.getPreviousValue(), undoneCell.getRow(), undoneCell.getColumn());
        }
        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    /**
     * If there is a value to redone, the UndoRedoManager returns the corresponding cell;
     * It's value is displayed again
     * @param event
     */
    @FXML
    void btnRedoClicked(MouseEvent event) {
        Cell redoneCell = movesManager.redoMove();
        if(redoneCell == null) {
            System.out.println("No move to redone");
            return;
        }

        modifyUserBoard(redoneCell.getValue(), redoneCell.getRow(), redoneCell.getColumn());
        drawOnCanvas(canvas.getGraphicsContext2D());
    }

    /**
     * On choosing different option from the levels, a new game is requested from the server
     * @param event
     */
    @FXML
    void optEasyClicked(MouseEvent event) {
        currentLevel = LevelType.EASY;
        startNewGame();
    }

    @FXML
    void optHardClicked(MouseEvent event) {
        currentLevel = LevelType.HARD;
        startNewGame();
    }

    @FXML
    void optMediumClicked(MouseEvent event) {
        currentLevel = LevelType.MEDIUM;
        startNewGame();
    }

    /**
     * Helper function to union the initial board and user board, so that user's solution can be checked
     * @return Board which contains cells' values from both boards
     */
    private Board boardsUnion() {
        Board unionBoard = new Board();
        unionBoard.createGrid();

        for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                int value = initialBoard.getCell(row, col).getValue();
                if(value != 0)
                    unionBoard.getCell(row, col).setValue(value);
                else {
                    value = userBoard.getCell(row, col).getValue();
                    unionBoard.getCell(row, col).setValue(value);
                }
            }
        }

        return unionBoard;
    }

    /**
     * On clicking the Check Input button, a union board is created from the inital and user board,
     * so that a full board can be send to the server for checking
     * If the puzzle is solved, the winning result is saved,
     * else an Alert is displayed, informing that the solution is not correct and the user can continue solving
     * @param event
     */
    @FXML
    void btnCheckInputClicked(MouseEvent event) {
        try {
            isSolved = sudokuServer.isSolution(boardsUnion());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(isSolved) {
            saveResults(GameOutcome.SOLVED);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sudoku Solved");
            alert.setHeaderText("Congratulations!");
            alert.setContentText("You successfully solved the puzzle!");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sudoku Unsolved");
            alert.setHeaderText("The puzzle is not correct!");
            alert.setContentText("Try again or start a new game.");
            alert.showAndWait();
        }
    }

    /**
     * On clicking the New Game button, if current game is solved, a request for new game is send to the server;
     * Else, is displayed an Alert for confirmation, because the game will be counted as unsolved.
     * On clicking OK, the request for new game is send to the server
     * @param event
     */
    @FXML
    void btnNewGameClicked(MouseEvent event) {
        // the game is solved already and would not count as unsolved
        if(isSolved) {
            startNewGame();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Start New Game");
        alert.setHeaderText("Are you sure you want to start a new game?");
        alert.setContentText("Current game will be counted as unsolved!");

        Optional<ButtonType> option = alert.showAndWait();

        if(option.isPresent())
            if(option.get() == ButtonType.OK) {

                saveResults(GameOutcome.UNSOLVED);
                startNewGame();
            }

    }

    /**
     * On clicking the Show Solution button, a confirmation Alert is displayed,
     * because the current game will be counted as unsolved.
     * On clicking OK, the correct solution is requested from the server and then displayed
     * @param event
     */
    @FXML
    void btnShowSolutionClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Show Solution");
        alert.setHeaderText("Are you sure you want to see the solution?");
        alert.setContentText("This game will be counted as unsolved!");

        Optional<ButtonType> option = alert.showAndWait();

        if(option.isPresent())
            if(option.get() == ButtonType.OK) {
                try {
                    userBoard = sudokuServer.showSolution();
                    saveResults(GameOutcome.UNSOLVED);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                isSolved = true;
                drawOnCanvas(canvas.getGraphicsContext2D());
            }
    }

    /**
     * On clicking the Exit button
     * @param event
     */
    @FXML
    void btnExitClicked(MouseEvent event) {
        if(!isSolved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Game");
            alert.setHeaderText("Are you sure you want to exit the game?");
            alert.setContentText("Current game will be counted as unsolved!");

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent())
                if (option.get() == ButtonType.OK)
                    saveResults(GameOutcome.UNSOLVED);
                else
                    return;
        }

        try {
            sudokuServer.recordStatistics(user);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("Error with recording statistics");
        }
        quitApp();
    }

    private void quitApp() {
        Platform.exit();
        System.exit(0);
    }
}