package Controller;


import Model.GameEngine;
import IView.IView;
import gamesave.GameSaver;
import java.util.*;

public class GameController implements IView.InputListener {

    private final GameEngine engine;
    private final IView view;
    private GameSaver gameSaver = new GameSaver();
    private final List<Integer> highScores = new ArrayList<>();
    private boolean highScoreSaved = false;


    public GameController(GameEngine engine, IView view) {
        this.engine = engine;
        this.view = view;

        view.setListener(this);


        engine.initGame();
        render();
    }

    private void render() {
        view.render(engine.getBoard(), engine.getScore(), engine.isGameOver());
    }

    private void afterMove(boolean moved) {
        if (moved) {
            render();
        }
        if (engine.isGameOver()) {
            view.showMessage("Game Over! Press R to restart.");

            if (!highScoreSaved) {
                highScoreSaved = true;

                try {
                    gameSaver.saveHighscore(engine.getScore());
                    view.showHighScores(gameSaver.loadHighscores()); // List<Integer>
                } catch (Exception e) {
                    view.showMessage("Could not save/load highscores");
                }
            }
            render();
        }
    }

    @Override public void onLeft()  { if (!engine.isGameOver()) afterMove(engine.moveLeft()); }
    @Override public void onRight() { if (!engine.isGameOver()) afterMove(engine.moveRight()); }
    @Override public void onUp()    { if (!engine.isGameOver()) afterMove(engine.moveUp()); }
    @Override public void onDown()  { if (!engine.isGameOver()) afterMove(engine.moveDown()); }

    @Override
    public void onRestart() {
        highScoreSaved = false;
        view.showMessage("Game restarted");
        engine.restart();
        render();
    }

    @Override
    public void onQuit() {
        view.showMessage("Game quit");
        view.close();
    }

    @Override
    public void onAuto() {
        if (engine.isGameOver()) {
            view.showMessage("Game Over");
            return;
        }
        boolean moved = engine.autoMove();
        if (moved) {
            render();
        }
    }

    @Override
    public void onSave() {
        try {
            gameSaver.save(engine.getBoard(), engine.getScore());
            view.showMessage("Game saved");
        } catch (Exception e) {
            view.showMessage("Save failed");
        }
    }

    @Override
    public void onLoad() {
        try {
            GameSaver.SavedGame sg = gameSaver.load();
            engine.setState(sg.board, sg.score);
            highScoreSaved = false;
            view.showMessage("Game loaded");
            render();
        } catch (Exception e) {
            view.showMessage("Load failed");
        }
    }

    @Override
    public void onUndo(){
        engine.undo();
        view.showMessage("Undo successful");
        render();
    }

}
