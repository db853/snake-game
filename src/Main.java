import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int boardWidth = 500;
        double boardHeight = 550;

        JFrame frame = new JFrame("Snake");
        frame.setSize(boardWidth, (int) boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame snake = new SnakeGame();
        frame.add(snake);
        frame.pack();
        frame.setVisible(true);

    }
}
