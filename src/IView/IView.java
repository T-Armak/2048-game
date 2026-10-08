package IView;

public interface IView {
    void setListener(InputListener listener);

    // Uppdaterar UI med senaste state från engine.
    void render(int[][] board, int score, boolean gameOver);

    void showMessage(String message);

    void showHighScores(java.util.List<Integer> scores);


    // Stäng UI.
    void close();

    interface InputListener {
        void onLeft();

        void onRight();

        void onUp();

        void onDown();

        void onRestart();

        void onQuit();

        // C-nivå
        void onAuto();

        // A -nivå
        void onUndo();   //
        void onSave();
        void onLoad();
    }
}
