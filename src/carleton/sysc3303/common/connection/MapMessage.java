package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.Tile;

public class MapMessage implements IMessage
{
    private Tile[][] tiles;


    /**
     * Constructor.
     *
     * @param walls
     */
    public MapMessage(Tile[][] tiles)
    {
        this.tiles = tiles;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MapMessage(String data)
    {
        String[] rows = data.split("\\|");
        tiles = new Tile[rows.length][];
        Tile[] tmp = Tile.values();

        for(int i=0; i<rows.length; i++)
        {
            System.out.println(rows[i]);
            tiles[i] = new Tile[rows[i].length()];

            for(int j=0; j<rows[i].length(); j++)
            {
                tiles[i][j] = tmp[Integer.parseInt(""+rows[i].charAt(j))];
            }
        }
    }


    /**
     * Gets the data.
     *
     * @return
     */
    public Tile[][] getWalls()
    {
        return tiles;
    }


    @Override
    public String serialize()
    {
        StringBuilder out = new StringBuilder();

        for(int i=0; i<tiles.length; i++)
        {
            for(int j=0; j<tiles[i].length; j++)
            {
                out.append(tiles[i][j].ordinal());
            }

            out.append("|"); // separates rows
        }

        out.deleteCharAt(out.length() - 1); // deletes the extra "|"

        return out.toString();
    }

}
