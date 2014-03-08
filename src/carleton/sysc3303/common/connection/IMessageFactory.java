package carleton.sysc3303.common.connection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class IMessageFactory
{
    /**
     * Hidden constructor to prevent initialization.
     */
    private IMessageFactory()
    {
    }


    /**
     * Factory method for creating IMessage instances
     * from data that comes in packets.
     *
     * This was done in both client and server separately, so
     * this class was created.
     *
     * @param data
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static IMessage forge(byte[] data)
        throws NoSuchMethodException,
               SecurityException,
               ClassNotFoundException,
               InstantiationException,
               IllegalAccessException,
               IllegalArgumentException,
               InvocationTargetException
    {
        @SuppressWarnings("rawtypes")
        Constructor c;
        String[] msg = new String(data).trim().split(":");
        String cls = IMessage.class.getPackage().getName() + "." + msg[0];

        c = Class.forName(cls.toString()).getConstructor(new Class[]{String.class});
        return (IMessage)c.newInstance(msg[1]);
    }


    /**
     * Serialzes the message.
     *
     * @param m
     * @return
     */
    public static byte[] serialize(IMessage m)
    {
        String[] cls = m.getClass().getCanonicalName().split("\\.");
        String msg = cls[cls.length-1] + ":" + m.serialize();
        return msg.getBytes();
    }
}
