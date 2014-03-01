package carleton.sysc3303.client;

import carleton.sysc3303.client.connection.Position;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class GameView extends JPanel
{
    private static final long serialVersionUID = -823641346290407577L;
    public static final int BOARD_SIZE = 20;
    private Board board;


    /**
     * Constructor.
     */
    public GameView()
    {
        init();
    }


    /**
     * UI initialization.
     */
    private void init()
    {
        this.setLayout(new BorderLayout());

        board = new Board(BOARD_SIZE);
        add(board, BorderLayout.CENTER);

        setMap(new ArrayList<Position>());
    }


    /**
     * Set the internal board's walls.
     * This should probably called when an event comes
     * in from the connection.
     *
     * @param list
     */
    public void setMap(List<Position> list)
    {
        boolean[][] blocks = new boolean[BOARD_SIZE][BOARD_SIZE];

        for(int i=0; i<BOARD_SIZE; i++)
        {
            for(int j=0; j<BOARD_SIZE; j++)
            {
                blocks[i][j] = false;
            }
        }

        for(Position p: list)
        {
            blocks[p.getX()][p.getY()] = true;
        }

        board.setWalls(blocks);
        board.repaint();
    }
}
