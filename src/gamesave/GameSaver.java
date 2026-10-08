package gamesave;

import java.nio.file.*;
import java.util.*;

public class GameSaver {

    public class SavedGame {
        // SavedGame object
        public int[][] board;
        public int score;


        public SavedGame(int[][] board, int score) {
            this.board = board;
            this.score = score;
        }
    }

    public void save(int[][] board, int score) throws Exception {

        StringBuilder data = new StringBuilder();
        data.append(score).append("\n");
        for (int i = 0; i < 4; i++) {
            data.append(board[i][0]).append(" ").append(board[i][1]).append(" ")
                    .append(board[i][2]).append(" ").append(board[i][3]).append("\n");
        }
        Files.writeString(Paths.get("src/data/GameState.txt"), data.toString());
    }

    public SavedGame load() throws Exception {

        String[] lines = Files.readString(Paths.get("src/data/GameState.txt")).split("\n");
        int score = Integer.parseInt(lines[0]);
        int[][] board = new int[4][4];
        for (int i = 0; i < 4; i++) {
            String[] nums = lines[i + 1].split(" ");
            for (int j = 0; j < 4; j++)
                board[i][j] = Integer.parseInt(nums[j]);
        }
        return new SavedGame(board, score);
    }

    public void saveHighscore(int score) throws Exception {
        // Lägg till score som en ny rad
        Files.writeString(Paths.get("src/data/HighScores.txt"), score + "\n", StandardOpenOption.APPEND);

    }

    public List<Integer> loadHighscores() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/data/HighScores.txt"));
        List<Integer> scores = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
                if (!line.isEmpty())
            scores.add(Integer.parseInt(line));
        }
        scores.sort(Collections.reverseOrder());
        return scores;

    }


}
