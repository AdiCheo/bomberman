package carleton.sysc3303.common.connection;

public class StateMessage implements IMessage
{
    public enum State
    {
        NOTSTARTED,//Waiting for game to start
        STARTED,//Game as begun
        END//Game as ended
    };

    private State state;

    public StateMessage(State state)
    {
        this.state = state;
    }

    public StateMessage(String data)
    {
        String[] args = data.split(",");
        this.state = State.valueOf(args[0]);
    }

    public State getState()
    {
        return state;
    }

    public String serialize()
    {
        return state.toString();
    }
}
