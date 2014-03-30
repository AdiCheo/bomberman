package carleton.sysc3303.client.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import carleton.sysc3303.common.connection.ConfigureMessage;
import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.StateMessage;
import carleton.sysc3303.client.connection.IConnection;

public class SetupPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private int b, r, d, u;
	private IConnection c;
	JTextField numOfBombs, bombRange, delay, updateRate;
	JLabel numLabel, rangeLabel, delayLabel, updateLabel;
	JButton startButton, defaultButton;
	
	public SetupPanel(IConnection c)
	{
		super();
		this.c = c;
		
		initComponents();
	}
	
	public void initComponents()
	{
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		setLayout(layout);	
		
		//Initialize new JTextFields
		numOfBombs = new JTextField();
		numOfBombs.setText("     1");
		numOfBombs.setEditable(true);
		numOfBombs.setLocation(50, 0);
		numOfBombs.addActionListener(this);
		
		bombRange = new JTextField();
		bombRange.setText("     2");
		bombRange.setEditable(true);
		bombRange.addActionListener(this);
		
		delay = new JTextField();
		delay.setText("300");
		delay.setEditable(true);
		delay.addActionListener(this);
		
		updateRate = new JTextField();
		updateRate.setText("  50");
		updateRate.setEditable(true);
		updateRate.addActionListener(this);		
		
		//Initialize new JLabels
		numLabel = new JLabel("Insert number of bombs players can place at once");		
		rangeLabel = new JLabel("Insert the range of bombs");		
		delayLabel = new JLabel("Insert the delay between moves");		
		updateLabel = new JLabel("Insert the rate at which the server updates the game");		
		
		//Initialize new button
		startButton = new JButton("Start");
		startButton.setEnabled(true);
		startButton.setActionCommand("START");
		startButton.addActionListener(this);
		
		defaultButton = new JButton("Default");
		defaultButton.setEnabled(true);
		defaultButton.setActionCommand("DEFAULT");
		defaultButton.addActionListener(this);
		
		//Adding JTextField to GUI
		c.gridwidth = GridBagConstraints.BOTH;
		add(numOfBombs,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(numLabel,c);
		
		c.gridwidth = GridBagConstraints.BOTH;
		add(bombRange,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(rangeLabel,c);
		
		c.gridwidth = GridBagConstraints.BOTH;
		add(delay,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(delayLabel,c);

		c.gridwidth = GridBagConstraints.BOTH;
		add(updateRate,c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(updateLabel,c);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel(""));
		
		//Adding buttons to GUI
		c.gridwidth = GridBagConstraints.PAGE_START;
		add(startButton,c);
		c.gridwidth = GridBagConstraints.PAGE_END;
		add(defaultButton,c);
	}

	//Action when start button is pressed
	public void actionPerformed(ActionEvent a) 
	{
		IMessage m;
		
		if(a.getActionCommand() == "START")
		{
			b = Integer.parseInt(numOfBombs.getText().trim());
			r = Integer.parseInt(bombRange.getText().trim());
			d = Integer.parseInt(delay.getText().trim());
			u = Integer.parseInt(updateRate.getText().trim());
		}
		
		if(a.getActionCommand() == "DEFAULT")
		{
			numOfBombs.setText("1");
			bombRange.setText("2");
			delay.setText("250");
			updateRate.setText("50");
		}	
		
		m = new ConfigureMessage(b,r,d,u);
		c.queueMessage(m);
		
		try 
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		m = new StateMessage(StateMessage.State.STARTED);
		c.queueMessage(m);
	}
}
