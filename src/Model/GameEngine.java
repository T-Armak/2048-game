package Model;
import java.util.*;

import gamesave.GameSaver;

public class GameEngine {
    public int SIZE = 4;
    public int[][] board;
    public int score;
    public int[][] prevState;
    public int prevScore = 0;

    private boolean moved; // Track if any tile moved during last operation


    public GameEngine() {
        // initate variables
        this.board = new int[SIZE][SIZE];
        this.score = 0;
        this.prevState = new int[SIZE][SIZE];
        this.prevScore = 0;
    }



    // Initialize game with 2 random tiles
    public void initGame() {
        // spawn tiles upon initiation
        spawnNewTile();
        spawnNewTile();
    }

    public void setState(int[][] board, int score) {
        this.board = board;
        this.score = score;
    }

    public void undo() {
        if (prevState == null) return;
        for (int i = 0; i < SIZE; i++) {
            board[i] = prevState[i].clone();
        }
        score = prevScore;
    }

    // Move left: slide and merge all rows to the left
    public boolean moveLeft() {

        //it. ovr row, if changed save to file
        for (int i = 0; i < SIZE; i++) {
            prevState[i] = board[i].clone();
        }
        prevScore = score;
        moved = false;
        for (int row = 0; row < SIZE; row++) {
            int[] original = getRow(row);
            int[] processed = processLine(original);
            if (!arraysEqual(original, processed)) {
                moved = true;
            }
            setRow(row, processed);
        }
        if (moved) {
            spawnNewTile();
        }
        return moved;
    }

    public boolean moveRight() {
        for (int i = 0; i < SIZE; i++) {
            prevState[i] = board[i].clone();
        }
        prevScore = score;
        moved = false;
        for (int row = 0; row < SIZE; row++) {
            int[] original = getRow(row);
            int[] reversed = reverse(original);
            int[] processed = processLine(reversed);
            int[] result = reverse(processed);
            if (!arraysEqual(original, result)) {
                moved = true;
            }
            setRow(row, result);
        }
        if (moved) {
            spawnNewTile();
        }
        return moved;
    }

    // Move up: slide and merge all columns upward
    public boolean moveUp() {
        for (int i = 0; i < SIZE; i++) {
            prevState[i] = board[i].clone();
        }
        prevScore = score;
        moved = false;
        for (int col = 0; col < SIZE; col++) {
            int[] original = getColumn(col);
            int[] processed = processLine(original);
            if (!arraysEqual(original, processed)) {
                moved = true;
            }
            setColumn(col, processed);
        }
        if (moved) {
            spawnNewTile();
        }
        return moved;
    }

    // Move down: slide and merge all columns downward
    public boolean moveDown() {
        for (int i = 0; i < SIZE; i++) {
            prevState[i] = board[i].clone();
        }
        prevScore = score;
        moved = false;
        for (int col = 0; col < SIZE; col++) {
            int[] original = getColumn(col);
            int[] reversed = reverse(original);
            int[] processed = processLine(reversed);
            int[] result = reverse(processed);
            if (!arraysEqual(original, result)) {
                moved = true;
            }
            setColumn(col, result);
        }
        if (moved) {
            spawnNewTile();
        }
        return moved;
    }


    //Helpfunctions for moves

    // Process a single line: remove zeros, merge, add zeros at end
    private int[] processLine(int[] line) {
        // Step 1: Remove zeros
        int[] nonEmpty = removeZeros(line);

        // Step 2: Merge tiles
        int[] merged = mergeTiles(nonEmpty);

        // Step 3: Add zeros at the end to fill to SIZE
        int[] result = addZerosLast(merged);

        return result;
    }

    // Remove all zeros from array
    private int[] removeZeros(int[] line) {
        int count = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != 0) {
                count++;
            }
        }

        int[] result = new int[count];
        int index = 0;
        for (int i = 0; i < line.length; i++) {
            if (line[i] != 0) {
                result[index++] = line[i];
            }
        }
        return result;
    }

    // Merge adjacent equal tiles
    private int[] mergeTiles(int[] line) {
        if (line.length == 0) {
            return line;
        }

        int[] temp = new int[line.length];
        int index = 0;

        for (int i = 0; i < line.length; i++) {
            if (i + 1 < line.length && line[i] == line[i + 1]) {
                // Merge tiles
                int mergedValue = line[i] * 2;
                temp[index++] = mergedValue;
                score += mergedValue;
                i++; // Skip next tile (already merged)
            } else {
                temp[index++] = line[i];
            }
        }

        // Create result array with exact size
        int[] result = new int[index];
        for (int i = 0; i < index; i++) {
            result[i] = temp[i];
        }
        return result;
    }

    // Add zeros at the end to fill to SIZE
    private int[] addZerosLast(int[] line) {
        int[] result = new int[SIZE];
        for (int i = 0; i < line.length && i < SIZE; i++) {
            result[i] = line[i];
        }
        return result;
    }

    // Reverse array
    private int[] reverse(int[] line) {
        int[] result = new int[line.length];
        for (int i = 0; i < line.length; i++) {
            result[i] = line[line.length - 1 - i];
        }
        return result;
    }

    // Get a row from the board
    private int[] getRow(int row) {
        int[] result = new int[SIZE];
        for (int col = 0; col < SIZE; col++) {
            result[col] = board[row][col];
        }
        return result;
    }

    // Set a row on the board
    private void setRow(int row, int[] values) {
        for (int col = 0; col < SIZE; col++) {
            board[row][col] = values[col];
        }
    }

    // Get a column from the board
    private int[] getColumn(int col) {
        int[] result = new int[SIZE];
        for (int row = 0; row < SIZE; row++) {
            result[row] = board[row][col];
        }
        return result;
    }

    // Set a column on the board
    private void setColumn(int col, int[] values) {
        for (int row = 0; row < SIZE; row++) {
            board[row][col] = values[row];
        }
    }

    // Check if two arrays are equal
    private boolean arraysEqual(int[] a, int[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    // Spawn a new tile (2 or 4) at random empty position
    private void spawnNewTile() {
        // Find all empty positions
        int emptyCount = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount == 0) {
            return; // No empty tiles
        }

        // Choose a random empty position
        int randomEmpty = (int) (Math.random() * emptyCount);
        int currentEmpty = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    if (currentEmpty == randomEmpty) {
                        // 90% chance for 2, 10% chance for 4
                        board[row][col] = Math.random() < 0.9 ? 2 : 4;
                        return;
                    }
                    currentEmpty++;
                }
            }
        }
    }

    // Check if the game is over
    public boolean isGameOver() {
        // Game is not over if there are empty tiles
        if (!hasEmptyTiles() && !canMove()) {
            return true;
        }
        return false;
    }

    // Check if there are empty tiles
    private boolean hasEmptyTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if any move is possible
    private boolean canMove() {
        // Check horizontal
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE - 1; col++) {
                if (board[row][col] == board[row][col + 1]) {
                    return true;
                }
            }
        }

        // Check vertical
        for (int row = 0; row < SIZE - 1; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == board[row + 1][col]) {
                    return true;
                }
            }
        }

        return false;
    }

    // Getters
    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public int getSize() {
        return SIZE;
    }

    public void restart(){
        this.board = new int[SIZE][SIZE];
        this.score = 0;
        this.moved = false;
        initGame();
    }

    public boolean autoMove() {
        List<Integer> moves = new ArrayList<>();
        moves.add(0);
        moves.add(1);
        moves.add(2);
        moves.add(3);

        Collections.shuffle(moves);

        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i) == 0 && moveLeft()) {
                return true;
            }
            if (moves.get(i) == 1 && moveRight()) {
                return true;
            }
            if (moves.get(i) == 2 && moveUp()) {
                return true;
            }
            if (moves.get(i) == 3 && moveDown()) {
                return true;
            }
        }
        return false;
    }

}