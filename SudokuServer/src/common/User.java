package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SudokuServer/common: User.java
 * Represents a user, playing Sudoku
 */
public class User implements Serializable {
    /**
     * User has:
     *  - username
     *  - difficyltyLevelsPlayed - saves count how much times each level is played
     *  - totalTimePlayed - in milliseconds
     *  - gameOutcomeStats - saves statistics for solved/unsolved games
     */
    private String username;
    private Map<LevelType, Integer> difficultyLevelsPlayed;
    private long totalTimePlayed;
    private Map<GameOutcome, Integer> gameOutcomesStats;

    public User(String username, Map<LevelType, Integer> difficultyLevelsPlayed, long totalTimePlayed,
                Map<GameOutcome, Integer> gameOutcomesStats) {
        // Not sure if it makes sense
        setUsername(username);
        setDifficultyLevelsPlayed(difficultyLevelsPlayed);
        setTotalTimePlayed(totalTimePlayed);
        setGameOutcomesStats(gameOutcomesStats);
    }

    public User() {
        this(null, null, 0, null);
    }

    public User(String username) {
        this(username, null, 0, null);
    }

    public User(User otherUser) {
        this(otherUser.getUsername(), otherUser.getDifficultyLevelsPlayed(),
                otherUser.getTotalTimePlayed(), otherUser.getGameOutcomesStats());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if(username != null)
            this.username = username;
        else
            this.username = "unknown";
    }

    public Map<LevelType, Integer> getDifficultyLevelsPlayed() {
        return new HashMap<>(difficultyLevelsPlayed);
    }

    private void setDifficultyLevelsPlayed(Map<LevelType, Integer> difficultyLevelsPlayed) {
        if(difficultyLevelsPlayed != null)
            this.difficultyLevelsPlayed = new HashMap<>(difficultyLevelsPlayed);
        else
            this.difficultyLevelsPlayed = new HashMap<>();
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    private void setTotalTimePlayed(long totalTimePlayed) {
        if(totalTimePlayed > 0)
            this.totalTimePlayed = totalTimePlayed;
        else
            this.totalTimePlayed = 0;
    }

    public Map<GameOutcome, Integer> getGameOutcomesStats() {
        return new HashMap<>(gameOutcomesStats);
    }

    private void setGameOutcomesStats(Map<GameOutcome, Integer> gameOutcomesStats) {
        if(gameOutcomesStats != null)
            this.gameOutcomesStats = new HashMap<>(gameOutcomesStats);
        else
            this.gameOutcomesStats = new HashMap<>();
    }

    public String printLevels() {
        String print = "Levels\n";
        return print.concat(difficultyLevelsPlayed.entrySet().stream()
                .map(e -> " " + e.getKey() + ": " + e.getValue() + " game(s)")
                .collect(Collectors.joining("\n")));
    }

    public String printOutcomes() {
        String print = "Results\n";
        return print.concat(gameOutcomesStats.entrySet().stream()
                .map(e -> " " + e.getKey() + ": " + e.getValue() + " game(s)")
                .collect(Collectors.joining("\n")));
    }

    public String printTotalTime() {
        long totalSeconds = totalTimePlayed / 1000;
        long hours = (totalSeconds / 3600);
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;

        return String.format("Total time played: %02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public String toString() {
        /** Username
         *  Levels
         *      easy: N game(s)
         *      medium: N game(s)
         *      hard: N game(s)
         *  Results
         *      solved: N game(s)
         *      unsolved: N game(s)
         *  Total time played: HH:MM:SS
         */
        return String.format("%-10s%n%s%n%s%n%s%n",
                username, this.printLevels(), this.printOutcomes(), this.printTotalTime());
    }

    // adding the chosen difficulty level to the map
    public void addDifficultyLevel(LevelType levelValue) {
        if(difficultyLevelsPlayed.containsKey(levelValue))
            difficultyLevelsPlayed.put(levelValue, difficultyLevelsPlayed.get(levelValue) + 1);
        else
            difficultyLevelsPlayed.put(levelValue, 1);
    }

    /**
     * Adding time played for every game, all saved in totalTimePlayed
     * @param timePlayed
     */
    // adding time played for every game to total
    public void addTimePlayed(long timePlayed) {
        totalTimePlayed += timePlayed;
    }

    /**
     * Add an outcome of SudokuGame
     * @param outcome
     */
    public void addGameResult(GameOutcome outcome) {
        if(gameOutcomesStats.containsKey(outcome))
            gameOutcomesStats.put(outcome, gameOutcomesStats.get(outcome) + 1);
        else
            gameOutcomesStats.put(outcome, 1);
    }

}
