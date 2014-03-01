package carleton.sysc3303.common.connection;

/**
 * Interface that represents the messages sent between client and server.
 *
 * @author Kirill Stepanov
 */
public interface IMessage
{
    /**
     * Convert the message arguments to a string.
     * Do not include the message type.
     *
     * @return
     */
    public String serialize();
}
