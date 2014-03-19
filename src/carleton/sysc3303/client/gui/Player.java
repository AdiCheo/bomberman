package carleton.sysc3303.client.gui;

import carleton.sysc3303.common.Position;

public class Player extends Position
{
    private String name;
    private boolean isMonster;
    private int id;

    /**
     * Constructor.
     *
     * @param x
     * @param y
     * @param isMonster
     * @param name
     */
    public Player(int id, int x, int y, boolean isMonster, String name)
    {
        super(x, y);

        this.id = id;
        this.name = name;
        this.isMonster = isMonster;
    }


    /**
     * Gets the player's id.
     *
     * @return
     */
    public int getId()
    {
        return id;
    }


    /**
     * Gets the name.
     *
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * Is the player a monster?
     *
     * @return
     */
    public boolean isMonster()
    {
        return isMonster;
    }


    @Override
    public int hashCode()
    {
        return id;
    }


    @Override
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Player))
        {
            return false;
        }

        return id == ((Player)o).id;
    }
}
