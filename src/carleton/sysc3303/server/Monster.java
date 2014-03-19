package carleton.sysc3303.server;

import carleton.sysc3303.common.PlayerTypes;

public class Monster extends Player
{
    /**
     * Constructor.
     *
     * @param id
     */
    public Monster(int id)
    {
        super(id, "Monster");
    }


    @Override
    public PlayerTypes getType()
    {
        return PlayerTypes.MONSTER;
    }
}
