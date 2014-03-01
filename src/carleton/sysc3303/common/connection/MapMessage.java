package carleton.sysc3303.common.connection;

public class MapMessage implements IMessage
{
    private boolean[][] walls;


    /**
     * Constructor.
     *
     * @param walls
     */
    public MapMessage(boolean[][] walls)
    {
        this.walls = walls;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MapMessage(byte[] data)
    {
        String[] rows = new String(data).split("|");
        walls = new boolean[rows.length][];

        for(int i=0; i<rows.length; i++)
        {
            walls[i] = new boolean[rows[i].length()];

            for(int j=0; j<rows[i].length(); j++)
            {
                walls[i][j] = rows[i].charAt(j) == '1' ? true : false;
            }
        }
    }


    /**
     * Gets the data.
     *
     * @return
     */
    public boolean[][] getWalls()
    {
        return walls;
    }


    @Override
    public String serialize()
    {
        StringBuilder out = new StringBuilder();

        for(int i=0; i<walls.length; i++)
        {
            for(int j=0; j<walls[i].length; j++)
            {
                out.append(walls[i][j] ? "1" : "0");
            }

            out.append("|"); // separates rows
        }

        out.deleteCharAt(out.length() - 1); // deletes the extra "|"

        return out.toString();
    }

}
