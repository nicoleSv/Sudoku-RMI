package usergui;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * SudokuClient/usergui: TimerManager.java
 * Implements Timer which tracks how much time user solves sudoku and shows it in label object
 */
public class TimerManager {
    /**
     * TimerManager has:
     *  - lblTimer, where the elapsed time is displayed
     *  - suspended - boolean to flag whether the timer is stopped
     *  - milliseconds - elapsed time
     *  - thread - for simulating the elapsing time
     */
    private Label lblTimer;
    private boolean suspended;
    private long milliseconds;
    private Thread thread;

    public TimerManager(Label lblTimer) {
        if(lblTimer != null)
            this.lblTimer = lblTimer;
        else
            System.out.println("Error occurred with lblTimer");

        milliseconds = 0;
        createThread();

    }

    /**
     * Creates a thread which sleeps every one second and updates the given label
     * This way it simulates a timer
     */
    private void createThread() {
        thread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000);
                    synchronized (this) {
                        while(suspended)
                            wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Error with time thread.");
                }


                Platform.runLater(() -> lblTimer.setText(convertToTime(milliseconds)));
                milliseconds += 1000;
            }
        });
    }

    /**
     * Starts the thread for the first time, or wakes it up after being suspended
     */
    public void startTimer() {
        suspended = false;
        milliseconds = 0;

        synchronized (this) {
            if(thread.isAlive())
                notifyAll();
            else
                thread.start();
        }
    }

    /**
     * Stops the timer when needed
     */
    public void stopTimer() {
        suspended = true;
    }

    /**
     * Converts milliseconds to format hh:mm:ss for displaying in the GUI
     * @param milliseconds
     * @return
     */
    public String convertToTime(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = milliseconds / (1000 * 60 * 60);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public long getMilliseconds() {
        return milliseconds;
    }
}
