import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;


public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 500;
    double boardHeight = 550;
    int gridSize = 50;

    Image snakeHead;
    Image appleImage;
    int snakeHeadX = boardWidth/5;
    int snakeHeadY = (int)((double) (boardHeight/33 * 15));
    int snakeHeadWidth = 50;
    int snakeHeadHeight = 50;
    ArrayList<int[]> snakeBody= new ArrayList<>();


    public class Snake {
        int x = snakeHeadX;
        int y = snakeHeadY;
        int width = snakeHeadWidth;
        int height = snakeHeadHeight;
        Image image;

        Snake(Image image) {
            this.image = image;
        }

    }

    int appleX = 0;
    int appleY = 0;
    int drawAppleX;
    int drawAppleY;
    int appleWidth = 32;
    int appleHeight = 32;
    Random applePosition = new Random();

    public class Apple {
        int x = appleX;
        int y = appleY;
        int width = appleWidth;
        int height = appleHeight;
        Image image;

        Apple(Image image) {
           this.image = image;
        }
    }

    // game logic
    int velocityX = 0;
    int velocityY = 0;
    int newVelocityX = 0;
    int newVelocityY = 0;
    boolean gameStarted = false;
    boolean appleOnSnake = true;
    boolean appleEaten = false;
    int score = 0;
    boolean gameOver = false;
    boolean isFirstMove = true;
    boolean hitWall = false;



    Snake snake;
    Apple apple;
    Timer gameLoop;
    Timer snakeMovement;

    SnakeGame() {
       setPreferredSize(new Dimension(boardWidth, (int) boardHeight));
       setFocusable(true);
       addKeyListener(this);


       snakeHead = new ImageIcon(getClass().getResource("./snakehead.png")).getImage();
       appleImage = new ImageIcon(getClass().getResource("./apple.png")).getImage();

       snake = new Snake(snakeHead);
       apple = new Apple(appleImage);

       snakeBody.add(new int[]{snake.x, snake.y});
       snakeBody.add(new int[]{snake.x - 50, snake.y});

       snakeMovement = new Timer(300, new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               move();
           }
       });


       gameLoop = new Timer(1000/60, this);
       gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        setBackground(Color.decode("#87CEEB"));

        for (int i = 1; i < snakeBody.size(); i++) {
            int[] part = snakeBody.get(i);

            g.setColor(Color.decode("#16520d"));
            g.fillRect(part[0], part[1], gridSize, gridSize);

        }
        Graphics2D g2d = (Graphics2D) g.create();

        int headX = Math.min(Math.max(snakeBody.getFirst()[0], 0), boardWidth);
        int headY = Math.min(Math.max(snakeBody.getFirst()[1], 50), (int) boardHeight);

        double rotation = 0;

        if (velocityX == -1) {
            rotation = Math.PI;
        } else if (velocityY == 1) {
            rotation = Math.PI / 2;
        } else if (velocityY == -1) {
            rotation = -Math.PI / 2;
        }

        g2d.rotate(rotation, headX + (double) snake.width / 2, headY + (double) snake.height / 2);
        g2d.drawImage(snake.image,  headX, headY, snake.width, snake.height, null);
        g2d.dispose();

        while (appleOnSnake) {
            apple.x = applePosition.nextInt(0, 500 / gridSize) * gridSize;
            apple.y = applePosition.nextInt(50 / gridSize, 550 / gridSize) *  gridSize;

            appleOnSnake = false;

            for (int[] part: snakeBody) {
                if (part[0] == apple.x && part[1] == apple.y) {
                    appleOnSnake = true;
                    break;
                }
            }
        }

        // rounds the current (x, y) coordinates tp the nearest grid cell
        apple.x = (apple.x / gridSize) * gridSize;
        apple.y = (apple.y / gridSize) * gridSize;

        // uses the rounded values and adds an offset to each axis so the apple is always centered
        drawAppleX = apple.x + (gridSize - apple.width) / 2;
        drawAppleY = apple.y + (gridSize - apple.height) / 2;

        g.drawImage(apple.image, drawAppleX, drawAppleY, apple.width, apple.height, null);
        g.setColor(new Color(0, 0, 0, 100));

        for (int x = 0; x < boardWidth; x += gridSize) {
            g.drawLine(x, 50, x, (int)boardHeight);
        }

        for (int y = 50; y < boardHeight; y += gridSize) {
            g.drawLine(0, y, boardWidth, y);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        if (gameOver) {
            g.drawString("Game over, score: " + String.valueOf(score), 25, 35);
        } else {
            g.drawString("Score: " + String.valueOf(score), 25, 35);
        }

    }

    public void move() {
        velocityX = newVelocityX;
        velocityY = newVelocityY;

        int currentHeadX = snakeBody.getFirst()[0];
        int currentHeadY = snakeBody.getFirst()[1];

        int newHeadX = currentHeadX + velocityX * gridSize;
        int newHeadY = currentHeadY + velocityY * gridSize;
        ;

        Rectangle headRect = new Rectangle(newHeadX, newHeadY, snake.width, snake.height);
        Rectangle appleRect = new Rectangle(drawAppleX, drawAppleY, appleWidth, appleHeight);

        snakeBody.addFirst(new int[] {newHeadX, newHeadY});
        appleEaten = headRect.intersects(appleRect);

        if (!appleEaten) {
            snakeBody.removeLast();
        } else {
            appleEaten = false;
            appleOnSnake = true;
            score += 1;
            repaint();
        }

        int[] snakeHead = snakeBody.getFirst();;

        if (newHeadX == -50 && velocityX == -1) {
            hitWall = true;
        } else if (newHeadX == boardWidth && velocityX == 1) {
            hitWall = true;
        } else if (newHeadY == 0 && velocityY == -1){
            hitWall = true;
        } else if (newHeadY == boardHeight && velocityY == 1) {
            hitWall = true;
        }

        if (hitWall) {
            gameOver = true;
        }

        if (!isFirstMove) {
            for (int i = 1; i < snakeBody.size(); i++) {
                if (Arrays.equals(snakeHead, snakeBody.get(i))) {
                    gameOver = true;
                    break;
                }
            }
        } else {
            isFirstMove = false;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) {
            repaint();
        }

        if (gameOver) {
            gameLoop.stop();
            snakeMovement.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                if (!gameOver) {
                    snakeMovement.start();
                }

                if (!gameStarted) {
                    gameStarted = true;
                }
                if (velocityX != -1) {
                    newVelocityX = 1;
                    newVelocityY = 0;
                }
                break;

            case KeyEvent.VK_LEFT:
                if (!gameOver) {
                    snakeMovement.start();
                }

                if (!gameStarted) {
                    gameStarted = true;
                }
                if (velocityX != 1) {
                    newVelocityX = -1;
                    newVelocityY = 0;
                }
                break;

            case KeyEvent.VK_UP:
                if (!gameOver) {
                    snakeMovement.start();
                }

                if (!gameStarted) {
                    gameStarted = true;
                }
                if (velocityY != 1) {
                    newVelocityY = -1;
                    newVelocityX = 0;
                }
                break;

            case KeyEvent.VK_DOWN:
                if (!gameOver) {
                    snakeMovement.start();
                }

                if (!gameStarted) {
                    gameStarted = true;
                }
                if (velocityY != -1) {
                    newVelocityY = 1;
                    newVelocityX = 0;
                }
                break;

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }
}
