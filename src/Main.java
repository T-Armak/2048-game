import Controller.GameController;
import Model.GameEngine;
import View.UserInterface;

public class Main {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        UserInterface view = new UserInterface();
        new GameController(engine, view);
    }
}
