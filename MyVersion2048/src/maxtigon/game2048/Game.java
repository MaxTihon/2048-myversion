package maxtigon.game2048;

import maxtigon.game2048.controller.Controller;
import maxtigon.game2048.model.Model;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        JFrame game = new JFrame();

        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setTitle("2048 MyVersion");
        game.setSize(450, 525);
        game.setResizable(false);

        game.add(controller.getView());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
