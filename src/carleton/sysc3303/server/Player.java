package carleton.sysc3303.server;
//import java.awt.Color;
import java.awt.event.*;

/**
 * Temporarily commented out since it is a UI class.
 */
public class Player //implements KeyListener
{
    /*private String name; //Players name
    private char avatar; //Players avatar on the board
    private int pNumber; //Players number, order in which he joined the game(used to determine starting position
    //int lives;//Number of lives a player as
    GameBoard board;//The board the player is playing on
    //Color color;//The players color
    Position p;//The players position on the board

    //Players are created when they connect to the game
    //The board is generated after all players are connected, use setBoard(Game_Board game) method to give players the board
    public Player(String playerName,int number)
    {
        name = playerName;
        avatar = name.toLowerCase().charAt(0);
        pNumber = number;
        //lives = 3;

        //Determine the starting location of a player
        //First player to join
        if(pNumber == 1)
        {
            p.x = 0;
            p.y = 0;
        }

        else
        {
            //Second player to join
            if(pNumber == 2)
            {
                p.x = 7;
                p.x = 7;
            }

            else
            {
                //Third player to join
                if(pNumber == 3)
                {
                    p.x = 0;
                    p.y = 7;
                }

                //Fourth player to join
                else
                {
                    p.x = 7;
                    p.y = 0;
                }
            }
        }
    }

    //Server will send players(clients) the board onces its been created
    public void setBoard(GameBoard game)
    {
        board = game;
    }

    //Get functions for various information about the player
    public Position getPosition()
    {
        return p;
    }

    public String getName()
    {
        return name;
    }

    public char getAvatar()
    {
        return avatar;
    }


    public void keyReleased(KeyEvent e)
    {
        //Leave this empty
    }

    public void keyTyped(KeyEvent e)
    {
        //Leave this empty
    }

    //Key pressed event for moving
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_UP)
        {
            //up arrow pressed
            move(p.x,p.y+1);
            //Send server move request
        }

        if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            //down arrow pressed
            move(p.x,p.y-1);
            //Send server move request
        }

        if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            //left arrow pressed
            move(p.x-1,p.y);
            //Send server move request
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            //right arrow pressed
            move(p.x+1,p.y);
            //Send server move request
        }

        if(e.getKeyCode() == KeyEvent.VK_B)
        {
            //Place bomb
            //placeBomb(p.x,p.y);
        }

    }

    //Move function for the player, invoked when arrow keys are pressed
    //Move function is responsible for signaling the server a move is happening
    //Either the move function or individual tiles have to be synchronized
    public void move(int x, int y)
    {
        //Check if move is valid
        //Needs to be synchronized
        if(board.isMoveValid(p.x, p.y, x, y))
        {
            //set current position to empty
            board.setTile(x, y, 'E');

            //move player
            p.x = x;
            p.y = y;

            //set the new position to be occupied by the player
            board.setTile(x, y, avatar);

            //Signal server clients player x is moving to position (x,y)

            //check if player as found the exit
            board.isExit(this);
        }

        else
        {
            System.out.println("INVALID MOVE");
            //invalid move
            //Signal logger of invalid move
        }
    }

    /*
    public void placeBomb(int x, int y)
    {
        //Place holder
    }

    public void death()
    {
        //If last life
        if(lives == 1)
            System.out.println(name + "has lost");
            //Signal server player as no more lives
            //Signal logger player as no mroe lives
            //End game for current player only

        //Else remove 1 life
        else
            lives--;
    }
        */
}
