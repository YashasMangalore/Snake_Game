import javax.swing.*;

public class SnakeGame extends JFrame
{
    Board board;
    SnakeGame()
    {//Constructor
        board=new Board();
        add(board);
        pack();
        setResizable(false);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }
    public static void main(String[] args)
    {//Initialize snake game object
        if(JOptionPane.showConfirmDialog(null,"Do you want to start the game?",
                "Snake Game",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION)
        {
            SnakeGame snakeGame=new SnakeGame();
        }
    }
}