package carleton.sysc3303.client.connection;

import java.util.EventListener;

public interface UserMessageListener extends EventListener
{
    public void newMessage(String s);
}
