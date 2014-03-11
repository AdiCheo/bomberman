package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.*;

public class BombMessage implements IMessage
{
    /**
     * Constructor.
     */
    public BombMessage()
    {

    }


    @Override
    public String serialize()
    {
        return String.format("%s", "BOMB");
    }

}
