package Demos;

// This is a simple interactive demo program which demonstrates the functionality of the Graphical Thread Manager.
//
// The program uses an instance of GraphicalThreadManager (from telhai.java.gthreads package) to monitor several instances
// of NullThread and RandThread objects (from telhai.java.gthreads.examples package).
//
// The NullThread instances are monitored as threads and the user gets to see their lifelines, including their active
// periods and their sleeping periods. The RandThread instances are monitored as objects, and the user gets to see how
// their fields change over time.
//
// The user can add NullThread and RandThread instances to the manager and remove them freely, the only limitation
// being the maximum number of threads/objects which can be monitored at the same time.
//
// Overall, this interactive demo gives a good feel of the abilities, interface and look of the Graphical Thread Manager.
//
// The interactive demo consists of two windows. One is the window of the GraphicalThreadManager, where it runs and
// monitors the various threads and object. The other window is a control panel with buttons, checkboxes and text fields,
// which gives the user easy access to most of the functions of the GraphicalThreadManager. Most of the options in the
// control panel are self-explanatory and tool tips provide the rest of the needed information.
//
// The user can test the various functions in any order, and terminate the demo by closing the control panel window or
// by clicking the exit button.
//

import telhai.java.gthreads.*;				// The basic GraphicalThreadManager package
import telhai.java.gthreads.examples.*;		// The NullThread and RandThread examples

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GTMDemo extends JFrame implements ActionListener
{
	private GraphicalThreadManager GTM;		// An instance of the Graphical Thread Manager
	
	private NullThread[] nullthr;			// Array of references to threads to monitor
	private RandThread[] randthr;			// Array of references to objects to monitor
	
	private int nullCount;					// Running counter of NullThread instances
	private int randCount;					// Running counter of RandThread instances

	private JButton addThread;				// Button to add a NullThread instance to the GTM (as THREAD to monitor)
	private JButton removeThread;			// Button to remove a NullThread instance from the GTM
	private JButton removeAllThreads;		// Button to remove all NullThread instances from the GTM
	
	private JTextField nullthrCount;		// Text field to hold the upper counting limit of the NullThread
	private JLabel labelCount;
	private JTextField nullthrDelay;		// Text field to hold the length of the sleep period of the NullThread
	private JLabel labelDelay;
	private JTextField nullthrTimes;		// Text field to hold the number of required iterations of the NullThread
	private JLabel labelTimes;
	private JCheckBox nullthrSleep;			// Checkbox to indicate whether the NullThread should start asleep
	
	private JTextField removeThrIndex;		// Text field to hold the index of the NullThread to remove
	private JLabel labelThrIndex;

	private JButton addObject;				// Button to add a RandThread instance to the GTM (as OBJECT to monitor)
	private JButton removeObject;			// Button to remove a RandThread instance from the GTM
	private JButton removeAllObjects;		// Button to remove all RandThread instances from the GTM

	private JCheckBox randInt;				// Checkbox to indicate whether the RandThread should generate integers
	private JCheckBox randLong;				// Checkbox to indicate whether the RandThread should generate long integers
	private JCheckBox randFloat;			// Checkbox to indicate whether the RandThread should generate floats
	private JCheckBox randDouble;			// Checkbox to indicate whether the RandThread should generate doubles
	
	private JTextField removeObjIndex;		// Text field to hold the index of the RandThread to remove
	private JLabel labelObjIndex;

	private JButton start;					// Button to start the Graphical Thread Manager.
	private JButton stop;					// Button to stop (pause) the Graphical Thread Manager.
	private JButton reset;					// Button to reset the Graphical Thread Manager.
	private JButton restart;				// Button to restart (reset+start) the Graphical Thread Manager.

	private JCheckBox autoReset;			// Checkbox to determine whether the GTM should automatically restart
	private JTextField speedField;			// Text field to determine the desired speed of the GTM timeline
	private JLabel speedLabel;

	private JButton exit;					// Exit button

	private JTextField errorText;			// Text field to display possible input errors

	public GTMDemo()		// The constructor creates an instance of the GTM and initializes the control panel window
	{
		setTitle("Graphical Thread Manager Interactive Demo");
		setSize(600,260);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		GTM = new GraphicalThreadManager();
		GTM.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);	// Cannot close just the GTM!

		nullCount = randCount = 0;						// Initialize the thread/object counter

		nullthr = new NullThread[GTM.MAX_THREADS];		// Allocate array for maximum simultaneous threads
		randthr = new RandThread[GTM.MAX_OBJECTS];		// Allocate array for maximum simultaneous objects

		addInterface();			// Initialize the control panel window - add buttons, checkboxes, text fields, labels
		addToolTips();			// Add tool tips to the components that need them
								
		setVisible(true);
	}

	private void addInterface()		// This method initializes all interface components and adds them to the window
	{
		Container c = getContentPane();

		Insets zeroMargin = new Insets(0,0,0,0);	// Buttons will be created with zero margins to save space

		addThread = new JButton("Add Counting Thread");
		addThread.setFont(addThread.getFont().deriveFont((float)10.0));
		addThread.setMargin(zeroMargin);
		addThread.addActionListener(this);			// Register this class as listener to the "Add Thread" button
		c.add(addThread);
		
		labelCount = new JLabel("Count to:");
		labelCount.setFont(labelCount.getFont().deriveFont((float)9.0));
		c.add(labelCount);
		
		labelDelay = new JLabel("Sleep (ms):");
		labelDelay.setFont(labelDelay.getFont().deriveFont((float)9.0));
		c.add(labelDelay);
		
		labelTimes = new JLabel("Iterations (0 = forever):");
		labelTimes.setFont(labelTimes.getFont().deriveFont((float)9.0));
		c.add(labelTimes);

		nullthrCount = new JTextField(new Integer(NullThread.DEFAULT_COUNT).toString());	// Default value for counter
		c.add(nullthrCount);

		nullthrDelay = new JTextField(new Integer(NullThread.DEFAULT_SLEEP).toString());	// Default value for sleep
		c.add(nullthrDelay);

		nullthrTimes = new JTextField("0");
		c.add(nullthrTimes);

		nullthrSleep = new JCheckBox("Start in sleeping mode:",false);			// Initial sleep is disabled by default
		nullthrSleep.setFont(nullthrSleep.getFont().deriveFont((float)9.0));
		nullthrSleep.setHorizontalTextPosition(AbstractButton.LEADING);
		c.add(nullthrSleep);

		removeThread = new JButton("Remove Thread");
		removeThread.setFont(addThread.getFont().deriveFont((float)10.0));
		removeThread.setMargin(zeroMargin);
		removeThread.setEnabled(false);				// Remove button is disabled by default (nothing to remove!)
		removeThread.addActionListener(this);		// Register this class as listener to the "Remove Thread" button
		c.add(removeThread);

		labelThrIndex = new JLabel("Thread to remove (0-"+(GTM.MAX_THREADS-1)+"):");
		labelThrIndex.setFont(labelThrIndex.getFont().deriveFont((float)9.0));
		c.add(labelThrIndex);

		removeThrIndex = new JTextField();
		c.add(removeThrIndex);

		removeAllThreads = new JButton("Remove All Threads");
		removeAllThreads.setFont(addThread.getFont().deriveFont((float)10.0));
		removeAllThreads.setMargin(zeroMargin);
		removeAllThreads.setEnabled(false);			// Remove button is disabled by default (nothing to remove!)
		removeAllThreads.addActionListener(this);	// Register this class as listener to the "Remove All Threads" button
		c.add(removeAllThreads);

		addObject = new JButton("Add Random Number Generator");
		addObject.setFont(addObject.getFont().deriveFont((float)10.0));
		addObject.setMargin(zeroMargin);
		addObject.addActionListener(this);			// Register this class as listener to the "Add Object" button
		c.add(addObject);
													// By default RandThread objects will generate all types of numbers
		randInt = new JCheckBox("Integer",true);
		randInt.setFont(randInt.getFont().deriveFont((float)9.0));
		c.add(randInt);

		randLong = new JCheckBox("Long integer",true);
		randLong.setFont(randLong.getFont().deriveFont((float)9.0));
		c.add(randLong);

		randFloat = new JCheckBox("Single precision floating point",true);
		randFloat.setFont(randFloat.getFont().deriveFont((float)9.0));
		c.add(randFloat);

		randDouble = new JCheckBox("Double precision floating point",true);
		randDouble.setFont(randDouble.getFont().deriveFont((float)9.0));
		c.add(randDouble);

		removeObject = new JButton("Remove Generator");
		removeObject.setFont(addObject.getFont().deriveFont((float)10.0));
		removeObject.setMargin(zeroMargin);
		removeObject.setEnabled(false);				// Remove button is disabled by default (nothing to remove!)
		removeObject.addActionListener(this);		// Register this class as listener to the "Remove Object" button
		c.add(removeObject);

		labelObjIndex = new JLabel("Generator to remove (0-"+(GTM.MAX_OBJECTS-1)+"):");
		labelObjIndex.setFont(labelObjIndex.getFont().deriveFont((float)9.0));
		c.add(labelObjIndex);

		removeObjIndex = new JTextField();
		c.add(removeObjIndex);

		removeAllObjects = new JButton("Remove All Generators");
		removeAllObjects.setFont(addObject.getFont().deriveFont((float)10.0));
		removeAllObjects.setMargin(zeroMargin);
		removeAllObjects.setEnabled(false);			// Remove button is disabled by default (nothing to remove!)
		removeAllObjects.addActionListener(this);	// Register this class as listener to the "Remove All Objects" button
		c.add(removeAllObjects);

		speedLabel = new JLabel("Speed ("+GTM.MIN_PPTICK+"-"+GTM.MAX_PPTICK+"):");
		speedLabel.setFont(speedLabel.getFont().deriveFont((float)9.0));
		c.add(speedLabel);

		speedField = new JTextField(GTM.MIN_PPTICK);	// Default speed is the minimal speed
		speedField.addActionListener(this);				// Register this class as listener to the "Speed" text field
		c.add(speedField);

		autoReset = new JCheckBox("Restart automatically",true);
		autoReset.setFont(autoReset.getFont().deriveFont((float)9.0));
		autoReset.addActionListener(this);				// Register this class as listener to the "Auto-reset" checkbox
		c.add(autoReset);

		start = new JButton("START");
		start.setFont(start.getFont().deriveFont((float)14.0));
		start.setMargin(zeroMargin);
		start.setForeground(Color.GREEN.darker());
		start.addActionListener(this);					// Register this class as listener to the "START" button
		c.add(start);
		
		stop = new JButton("STOP");
		stop.setFont(stop.getFont().deriveFont((float)14.0));
		stop.setMargin(zeroMargin);
		stop.setForeground(Color.RED.darker());
		stop.setEnabled(false);							// "STOP" button can only be enabled after starting
		stop.addActionListener(this);					// Register this class as listener to the "STOP" button
		c.add(stop);

		reset = new JButton("RESET");
		reset.setFont(reset.getFont().deriveFont((float)14.0));
		reset.setMargin(zeroMargin);
		reset.setForeground(Color.MAGENTA.darker());
		reset.setEnabled(false);						// "RESET" button can only be enabled after starting
		reset.addActionListener(this);					// Register this class as listener to the "RESET" button
		c.add(reset);
		
		restart = new JButton("RESTART");
		restart.setFont(restart.getFont().deriveFont((float)14.0));
		restart.setMargin(zeroMargin);
		restart.setForeground(Color.CYAN.darker());
		restart.setEnabled(false);						// "RESTART" button can only be enabled after starting
		restart.addActionListener(this);				// Register this class as listener to the "RESTART" button
		c.add(restart);

		exit = new JButton("EXIT");
		exit.setFont(exit.getFont().deriveFont((float)12.0));
		exit.setMargin(zeroMargin);
		exit.addActionListener(this);					// Register this class as listener to the "EXIT" button
		c.add(exit);

		errorText = new JTextField();					// Text field to display errors
		errorText.setEditable(false);					// Cannot be edited by the user!
		errorText.setBackground(Color.WHITE);
		c.add(errorText);
	}

	private void addToolTips()		// This method adds tool tip text to all the components where it is needed
	{
		start.setToolTipText("Starts the Graphical Thread Manager.");
		stop.setToolTipText("Pauses the Graphical Thread Manager. " + 
							"All threads and objects will stay in their positions, but the timeline will stop.");
		reset.setToolTipText("Resets the Graphical Thread Manager. Clears all the timelines and thread messages.");
		restart.setToolTipText("Restarts the Graphical Thread Manager (RESET+START)");
		exit.setToolTipText("Exits this Interactive Demo.");
		speedField.setToolTipText("The speed at which the timeline of the Graphical Thread Manager moves.");
		speedLabel.setToolTipText("The speed at which the timeline of the Graphical Thread Manager moves.");
		autoReset.setToolTipText("Selects whether the timeline should start over when it reaches the end.");
		addObject.setToolTipText("Adds a random number generator to the monitored objects. " +
								"Use the checkboxes below to select which types of numbers to generate.");
		removeObject.setToolTipText("Remove one of the random number generators. " +
									"Type the index of the generator to remove in the text field below.");
		addThread.setToolTipText("Adds a simple counting thread to the threads monitored by the " +
								"Graphical Thread Manager.");
		removeThread.setToolTipText("Remove one of the counting threads. " +
									"Type the index of the thread to remove in the text field below.");
		nullthrDelay.setToolTipText("The length of the sleep period between iterations of the counting thread " +
									"(in milliseconds)");
		labelDelay.setToolTipText("The length of the sleep period between iterations of the counting thread " +
									"(in milliseconds)");
		errorText.setToolTipText("Error messages are displayed here.");
	}

	public void actionPerformed (ActionEvent e)		// This method is invoked to handle components' action events
	{
		Object src = e.getSource();		// Get the component that sent the event
		errorText.setText("");			// Initially clear any past errors

		if (src==exit)					// Exit button pressed
		{
			System.exit(0);
		}
		else if (src==start)			// Start button pressed
		{
			start.setEnabled(false);		// Disable start button, enable stop, reset and restart
			stop.setEnabled(true);
			reset.setEnabled(true);
			restart.setEnabled(true);
			GTM.start();
		}
		else if (src==stop)				// Stop button pressed
		{
			stop.setEnabled(false);			// Disable stop button, enable start, reset and restart
			start.setEnabled(true);
			reset.setEnabled(true);
			restart.setEnabled(true);
			GTM.stop();
		}
		else if (src==reset)			// Reset button pressed
		{
			reset.setEnabled(false);		// Enable start button, disable stop, reset and restart
			start.setEnabled(true);
			stop.setEnabled(false);
			restart.setEnabled(false);
			GTM.resetHard();				// Perform "hard" reset which also clears the running time
		}
		else if (src==restart)			// Restart button pressed
		{
			GTM.restartHard();				// Perform "hard" reset, then start
		}
		else if (src==autoReset)		// Auto-reset checkbox toggled
		{
			if (autoReset.isSelected())		// If it was selected, enable auto-reset, else disable it
				GTM.setAutoReset(true);
			else
				GTM.setAutoReset(false);
		}
		else if (src==speedField)		// New value entered at Speed field
		{
			int speed;

			try
			{
				speed = Integer.parseInt(speedField.getText());

				if (speed<GTM.MIN_PPTICK || speed>GTM.MAX_PPTICK)			// If the value is out of range
					errorText.setText("Error: speed value out of range.");	// Display error message
				else
					GTM.setPixelsPerTick(speed);							// Otherwise apply the new value
			}
			catch (NumberFormatException x)									// If the text is not a number
			{
				errorText.setText("Error: illegal numeric format for speed.");
			}
		}
		else if (src==addThread)		// Add Thread button pressed
		{
			int count,delay,times,numThreads;
		
			try
			{
				count = Integer.parseInt(nullthrCount.getText());	// Read from the counter limit field
				delay = Integer.parseInt(nullthrDelay.getText());	// Read from the sleep period field
				times = Integer.parseInt(nullthrTimes.getText());	// Read from the number of iterations field

				numThreads = GTM.getNumOfThreads();
															// Create new NullThread with given values
				nullthr[numThreads] = new NullThread(count,delay,times,new Integer(++nullCount).toString());
				
				nullthr[numThreads].setInitSleep(nullthrSleep.isSelected());	// Set "initial sleep" flag
				nullthr[numThreads].setMessages(true);							// Enable messages from thread
				nullthr[numThreads].setPriority(Thread.MIN_PRIORITY);			// Set to minimum priority
																				// (to go easy on the system)
				GTM.addThread(nullthr[numThreads]);
				nullthr[numThreads].start();				// Add thread to GTM and start it

				if (++numThreads==GTM.MAX_THREADS)
					addThread.setEnabled(false);	// If maximum number of threads reached, disable add button

				removeThread.setEnabled(true);
				removeAllThreads.setEnabled(true);	// Enable remove buttons
			}
			catch (NumberFormatException x)					// If one of the values was of invalid numeric format
			{
				errorText.setText("Error: illegal numeric format in thread values.");
			}
		}
		else if (src==removeThread)		// Remove Thread button pressed
		{
			int index,numThreads;

			try
			{
				index = Integer.parseInt(removeThrIndex.getText());		// Get the index of the thread to remove
				numThreads = GTM.getNumOfThreads();

				if (index<0 || index>numThreads-1)
					errorText.setText("Error: thread index out of bounds.");
				else
				{
					GTM.removeThread(index);					// Remove thread from GTM

					if (--numThreads==0)						// If no threads left, disable remove buttons
					{
						removeThread.setEnabled(false);
						removeAllThreads.setEnabled(false);
					}
					addThread.setEnabled(true);					// Enable add button
					
					nullthr[index].kill();				// Kill the thread just removed to save system resources
					
					int i;
					for (i=index;i<numThreads;++i)
						nullthr[i] = nullthr[i+1];		// Rearrange references to threads still active
					nullthr[i] = null;
				}
			}
			catch (NumberFormatException x)
			{
				errorText.setText("Error: illegal numeric format in thread index.");
			}
		}
		else if (src==removeAllThreads)	// Remove All Threads button pressed
		{
			int numThreads = GTM.getNumOfThreads();

			GTM.removeAllThreads();

			addThread.setEnabled(true);				// Enable add button
			removeThread.setEnabled(false);			// Disable remove buttons
			removeAllThreads.setEnabled(false);

			for (int i=0;i<numThreads;++i)
			{
				nullthr[i].kill();				// Kill all threads
				nullthr[i] = null;				// Reset all references
			}
		}
		else if (src==addObject)		// Add Object button pressed
		{
			int key=0;
			int numThreads = GTM.getNumOfObjects();
											// Determine the type of numbers to generate according to the checkboxes
			if (randInt.isSelected())
				key+=RandThread.R_I;
			if (randLong.isSelected())
				key+=RandThread.R_L;
			if (randFloat.isSelected())
				key+=RandThread.R_F;
			if (randDouble.isSelected())
				key+=RandThread.R_D;

			randthr[numThreads] = new RandThread(key,new Integer(++randCount).toString());	// Create new generator
			randthr[numThreads].setPriority(Thread.MIN_PRIORITY);		// Set priority to minimum
			randthr[numThreads].start();								// Start thread

			GTM.addObject(randthr[numThreads]);				// Add as object to the GTM to monitor the fields

			if (++numThreads==GTM.MAX_OBJECTS)
				addObject.setEnabled(false);		// If maximum number of objects reached, disable add button

			removeObject.setEnabled(true);
			removeAllObjects.setEnabled(true);		// Enable remove buttons
		}
		else if (src==removeObject)		// Remove Object button pressed
		{
			int index,numThreads;

			try
			{
				index = Integer.parseInt(removeObjIndex.getText());		// Get the index of the object to remove
				numThreads = GTM.getNumOfObjects();

				if (index<0 || index>GTM.getNumOfObjects()-1)
					errorText.setText("Error: object index out of bounds.");
				else
				{
					GTM.removeObject(index);					// Remove the object from the GTM
			
					if (--numThreads==0)						// If no objects left, disable the remove buttons
					{
						removeObject.setEnabled(false);
						removeAllObjects.setEnabled(false);
					}
					addObject.setEnabled(true);					// Enable the add button

					randthr[index].kill();				// Kill the thread just removed to save system resources
					
					int i;
					for (i=index;i<numThreads;++i)
						randthr[i] = randthr[i+1];		// Rearrange references to threads still active
					randthr[i] = null;
				}
			}
			catch (NumberFormatException x)
			{
				errorText.setText("Error: illegal numeric format in object index.");
			}
		}
		else if (src==removeAllObjects)	// Remove All Objects button pressed
		{
			int numThreads = GTM.getNumOfObjects();

			GTM.removeAllObjects();

			addObject.setEnabled(true);				// Enable add button
			removeObject.setEnabled(false);			// Disable remove buttons
			removeAllObjects.setEnabled(false);

			for (int i=0;i<numThreads;++i)
			{
				randthr[i].kill();				// Kill all threads
				randthr[i] = null;				// Initialize references
			}
		}
	}

	public void validate()		// This method is used to validate the window -
	{							// Ensure proper position and size for all components
		super.validate();

		addThread.setBounds(10,10,150,20);
		labelCount.setBounds(10,30,50,20);
		labelDelay.setBounds(10,50,60,20);
		labelTimes.setBounds(10,70,120,20);
		nullthrCount.setBounds(60,30,100,20);
		nullthrDelay.setBounds(70,50,90,20);
		nullthrTimes.setBounds(130,70,30,20);
		nullthrSleep.setBounds(10,90,150,20);
		removeThread.setBounds(10,120,150,20);
		labelThrIndex.setBounds(10,140,120,20);
		removeThrIndex.setBounds(130,140,30,20);
		removeAllThreads.setBounds(10,170,150,20);
		addObject.setBounds(180,10,200,20);
		randInt.setBounds(180,30,200,20);
		randLong.setBounds(180,50,200,20);
		randFloat.setBounds(180,70,200,20);
		randDouble.setBounds(180,90,200,20);
		removeObject.setBounds(180,120,200,20);
		labelObjIndex.setBounds(180,140,150,20);
		removeObjIndex.setBounds(330,140,50,20);
		removeAllObjects.setBounds(180,170,200,20);
		speedLabel.setBounds(420,10,100,20);
		speedField.setBounds(520,10,40,20);
		autoReset.setBounds(420,40,140,20);
		start.setBounds(420,80,80,30);
		stop.setBounds(500,80,80,30);
		reset.setBounds(420,120,80,30);
		restart.setBounds(500,120,80,30);
		exit.setBounds(470,170,70,20);
		errorText.setBounds(10,200,400,20);
	}

	public static void main (String[] args)		// The main function starts the demo
	{
		GTMDemo demo = new GTMDemo();
	}
}
