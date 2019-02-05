package usergui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

/**
 *  SudokuClient/usergui: SudokuUser.java
 *  Launches the user GUI
 */
public class SudokuUser extends Application {
    /**
     * SudokuUser has:
     *  - username - to identify the user and save statistics
     */
    private static String username;

    static String getUsername() {
        return username;
    }

    private static void setUsername(String username) {
        if(username != null) SudokuUser.username = username;
        else SudokuUser.username = "";
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Text input dialog is displayed to ask for username input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sudoku");
        dialog.setHeaderText("Welcome to SudokuFX");
        dialog.setContentText("Enter a username:");
        Optional<String> enteredUsername = dialog.showAndWait(); // for creating User

        // If user does not click OK, the application shuts down
        if(!enteredUsername.isPresent()) {
            Platform.exit();
            System.exit(0);
        }

        setUsername(enteredUsername.get());

        // after identifying the user, the sudoku GUI is displayed
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        Scene scene = new Scene(root, 720, 500);
        primaryStage.setTitle("Sudoku");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
