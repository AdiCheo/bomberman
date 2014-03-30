package carleton.sysc3303.client.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.JFrame;

import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.client.connection.IConnection;
import carleton.sysc3303.client.connection.UDPConnection;

public class ConnectPanel extends JFrame implements ActionListener, Callable<IConnection>
{
    private static final long serialVersionUID = 1L;
    private JPanel panel;
    private IConnection c = null;
    private int field1, field2,field3,field4;
    private JTextField IP1, IP2, IP3, IP4;
    private JLabel IPLabel , dotLabel;
    private JButton connectButton;

    public ConnectPanel()
    {
        super();

        initComponents();
    }

    public void initComponents()
    {    	
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,420);
		setMinimumSize(getSize());
		setLayout(new BorderLayout());
				    
    	panel = new JPanel();
    	add(panel, BorderLayout.CENTER);    
    	
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
        panel.add(IPLabel);

        panel.add(IP1);
        panel.add(dotLabel);

        panel.add(IP2);
        panel.add(dotLabel);

        panel.add(IP3);
        panel.add(dotLabel);

        panel.add(IP4);
        panel.add(dotLabel);

        //Adding buttons to GUI
        panel.add(connectButton);
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

        if(field1 == 127 && field2 == 0 && field3 == 0 && field4 == 1)
        	IPAddress = "localhost";
        
        try 
        {
            c = new UDPConnection(InetAddress.getByName("localhost"), 9999);
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        }       
    }

	@Override
	public IConnection call() throws Exception 
	{
		setVisible(true);
		
		while(c == null)
		{
			Thread.sleep(1000);
		}	
		
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
