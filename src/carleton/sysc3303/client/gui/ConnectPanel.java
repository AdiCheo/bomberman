package carleton.sysc3303.client.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.client.connection.IConnection;
import carleton.sysc3303.client.connection.UDPConnection;

public class ConnectPanel extends JPanel implements ActionListener
{
    private static final long serialVersionUID = 1L;
    IConnection c = null;
    int field1, field2,field3,field4;
    JTextField IP1, IP2, IP3, IP4;
    JLabel IPLabel , dotLabel;
    JButton connectButton;

    public ConnectPanel()
    {
        super();

        initComponents();
    }

    public void initComponents()
    {
        //Initialize new JTextFields
        IP1 = new JTextField(3);
        IP1.setText("192");
        IP1.setEditable(true);
        IP1.setDocument(new JTextFieldLimit(3));
        IP1.addActionListener(this);

        IP2 = new JTextField(3);
        IP2.setText("168");
        IP2.setEditable(true);
        IP2.setDocument(new JTextFieldLimit(3));
        IP2.addActionListener(this);

        IP3 = new JTextField(3);
        IP3.setText("  0");
        IP3.setEditable(true);
        IP3.setDocument(new JTextFieldLimit(3));
        IP3.addActionListener(this);

        IP4 = new JTextField(3);
        IP4.setText("  1");
        IP4.setEditable(true);
        IP4.setDocument(new JTextFieldLimit(3));
        IP4.addActionListener(this);

        //Initialize new JLabels
        IPLabel = new JLabel("Please Insert IP address");
        dotLabel = new JLabel(".");

        //Initialize new button
        connectButton = new JButton("Connect");
        connectButton.setEnabled(true);
        connectButton.setActionCommand("CONNECT");
        connectButton.addActionListener(this);

        //Adding Components to GUI
        add(IPLabel);

        add(IP1);
        add(dotLabel);

        add(IP2);
        add(dotLabel);

        add(IP3);
        add(dotLabel);

        add(IP4);
        add(dotLabel);

        //Adding buttons to GUI
        add(connectButton);
    }

    //Action when start button is pressed
    public void actionPerformed(ActionEvent a)
    {
        IMessage m;
        String IPAddress;
        String dot = ".";

        if(a.getActionCommand() == "CONNECT")
        {
            field1 = Integer.parseInt(IP1.getText());
            field2 = Integer.parseInt(IP2.getText());
            field3 = Integer.parseInt(IP3.getText());
            field4 = Integer.parseInt(IP4.getText());
        }


        IPAddress = new String(Integer.toString(field1)) + dot + new String(Integer.toString(field2))
                  + dot +  new String(Integer.toString(field3)) + dot + new String(Integer.toString(field4));


        try {
            c = new UDPConnection(InetAddress.getByName(IPAddress), 9999);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        m = new MetaMessage(Type.CONNECT, "1," + "p");
        c.queueMessage(m);
        //Wait for reply
    }

    public IConnection getClient()
    {
        return c;
    }
}

//Class to force maximum # of characters in JTextField objects
class JTextFieldLimit extends PlainDocument
{

    private static final long serialVersionUID = 1L;
    private int limit;

    JTextFieldLimit(int limit)
    {
        super();
        this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
    {
        if(str == null)
            return;

        if((getLength() + str.length()) <= limit)
        {
            super.insertString(offset, str, attr);
        }
    }

}
