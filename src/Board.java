import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class Board extends JPanel implements ActionListener
{
    int boardHeight = 400;
    int boardWidth = 400;
    int dots;
    int dotSize = 10;
    int maxDots = ((boardHeight * boardWidth) / (dotSize * dotSize)); // 400*400/10*10
    int[] x = new int[maxDots];
    int[] y = new int[maxDots];
    int apple_x;
    int apple_y;
    int score;
    // Image
    Image body, apple, head;
    Timer timer;
    int DELAY = 150;
    boolean leftDirection = true;
    boolean rightDirection = false;
    boolean upDirection = false;
    boolean downDirection = false;
    boolean inGame = true;
    private int highScore = 0;
    private final File highScoreFile = new File("highScore.txt");

    Board()
    {
        TAdapter tAdapter = new TAdapter();
        addKeyListener(tAdapter);
        setFocusable(true);
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        initGame();
        loadImages();
        loadHighScore();
    }
    // Initialize game
    public void initGame()
    {
        dots = 3;
        x[0] = 250;
        y[0] = 250;
        // Initialize snake position
        for (int i = 1; i < dots; i++)
        {
            x[i] = x[0] + dotSize * i;
            y[i] = y[0];
        }
        locateApple();
        timer = new Timer(DELAY, this);
        timer.start();
    }
    // Load images from resources to Image object
    public void loadImages()
    {
        ImageIcon bodyIcon = new ImageIcon("src/resources/dot.png");
        body = bodyIcon.getImage();
        ImageIcon headIcon = new ImageIcon("src/resources/head.png");
        head = headIcon.getImage();
        ImageIcon appleIcon = new ImageIcon("src/resources/apple.png");
        apple = appleIcon.getImage();
    }
    // Draw images at snake and apple position from loadImages() loaded images
    @Override // existing method override
    public void paintComponent(Graphics g) // custom painting
    {
        super.paintComponent(g);
        doDrawing(g);
    }
    public void doDrawing(Graphics g)
    {
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[0], y[0], this);// 0=head
                } else {
                    g.drawImage(body, x[i], y[i], this);
                }
            }
        }
        else
        {
            gameOver(g);
            timer.stop();
        }
    }
    // Randomize apple position
    public void locateApple()
    {
        apple_x = ((int) (Math.random() * 39)) * dotSize;
        apple_y = ((int) (Math.random() * 39)) * dotSize;
    }
    // Check collision with border and body
    public void checkCollision() {
        // Collision with body
        for (int i = 1; i < dots; i++) {
            if ((i >= 4) && x[0] == x[i] && (y[0] == y[i]))
            {
                inGame = false;
                break;
            }
        }
        // Collision with head
        if (x[0] < 0 || y[0] < 0 || x[0] >= boardWidth || y[0] >= boardHeight) {
            inGame = false;
        }
    }
    private void increaseSpeed()
    {
        int newDelay = DELAY - (dots - 3) * 5; // Adjust this factor to control the speed increase
        if (newDelay < 50)
        {
            newDelay = 50; // Set a minimum delay to avoid the game becoming too fast
        }
        timer.setDelay(newDelay);
    }
    // Make snake eat food
    public void checkApple()
    {
        if (apple_x == x[0] && apple_y == y[0])
        {
            dots++;
            locateApple();
            increaseSpeed(); // Call increaseSpeed() when the snake eats an apple
        }
    }
    public void gameOver(Graphics g)
    {
        String msg = "Game Over!";
        score = (dots - 3) * 10;
        String scoreMsg = "Your Score: " + score;
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fontMetrics = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (boardWidth - fontMetrics.stringWidth(msg)) / 2, boardHeight / 4);// message,x,y
        g.drawString(scoreMsg, (boardWidth - fontMetrics.stringWidth(scoreMsg)) / 2, 3 * (boardHeight / 4));

        // Draw high score
        String highScoreMsg = "High Score: " + highScore;
        g.drawString(highScoreMsg, (boardWidth - fontMetrics.stringWidth(highScoreMsg)) / 2, 3*(boardHeight / 4) - 20);

        // Add code to prompt for restart
        String restartMsg = "Press Enter to Restart";
        g.drawString(restartMsg, (boardWidth - fontMetrics.stringWidth(restartMsg)) / 2, boardHeight / 2);
    }
    // Method to reset the game state
    public void resetGame()
    {
        inGame = true;
        dots = 3;
        leftDirection = true;
        rightDirection = false;
        upDirection = false;
        downDirection = false;
        initGame();
        timer.setDelay(DELAY);
        timer.restart();
        repaint();
        saveHighScore();
        if (score > highScore)
        {
            highScore = score;
            saveHighScore();
        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (inGame)
        {
            checkApple();
            checkCollision();
            move();
        }
        repaint(); // updates -- rebuilds
    }
    // Make snake move
    public void move()
    {
        for (int i = dots - 1; i >= 1; i--)
        {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (leftDirection)
        {
            x[0] = x[0] - dotSize;
        }
        else if (rightDirection)
        {
            x[0] = x[0] + dotSize;
        }
        else if (upDirection)
        {
            y[0] = y[0] - dotSize;
        }
        else
        {
            y[0] = y[0] + dotSize;
        }
    }
    // Implements controls
    private class TAdapter extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent keyEvent)
        {
            int key = keyEvent.getKeyCode();
            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            } else if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            } else if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            } else if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
            // code to handle restart
            if (!inGame && key == KeyEvent.VK_ENTER) {
                resetGame();
            }
        }
    }
    private void loadHighScore()
    {
        if (highScoreFile.exists())
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(highScoreFile))) {
                highScore = Integer.parseInt(reader.readLine());
            } catch (IOException | NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void saveHighScore()
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile))) {
            writer.write(Integer.toString(highScore));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}