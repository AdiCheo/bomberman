package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.PlayerTypes;


/**
 * Meta communication between client and server.
 *
 * @author Kirill Stepanov
 */
public class MetaMessage implements IMessage
{
    public enum Type
    {
        CONNECT,		// a client is connecting
        DISCONNECT,		// a client is disconnecting or the server dropping a client (after accepting)
        ACCEPT,			// the server acknowledging the new client
        REJECT,			// the server rejecting the client
        PING,			// the server testing connectivity
        PONG,			// the client responding to connectivity test
        USER_NOTICE		// a message to display to the user
    };


    private Type type;
    private String message;


    /**
     * Constructor.
     *
     * @param type
     */
    public MetaMessage(Type type)
    {
        this(type, "");
    }


    /**
     * Constructor.
     *
     * @param type
     * @param message
     */
    public MetaMessage(Type type, String message)
    {
        this.type = type;
        this.message = message;
    }


    /**
     * Creates a message that indicates you want to connect
     * as a spectator only.
     *
     * @return
     */
    public static MetaMessage connectSpectator()
    {
        return new MetaMessage(Type.CONNECT, "0");
    }


    /**
     * Creates a message that indicates you want to connect
     * as a regular player.
     *
     * @param type
     * @return
     */
    public static MetaMessage connectPlayer(PlayerTypes type)
    {
        String clientType;

        if(type == PlayerTypes.PLAYER)
        {
            clientType = "p";
        }
        else
        {
            clientType = "m";
        }

        return new MetaMessage(Type.CONNECT, "1," + clientType);
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MetaMessage(String data)
    {
        String[] args = data.split(",", 2);
        this.type = Type.valueOf(args[0]);
        this.message = args.length == 1 ? "" : args[1];
    }


    /**
     * Gets the type.
     *
     * @return
     */
    public Type getStatus()
    {
        return type;
    }


    /**
     * Get the message
     *
     * @return
     */
    public String getMessage()
    {
        return message;
    }


    @Override
    public String serialize()
    {
        return type.toString() + "," + message;
    }
}
