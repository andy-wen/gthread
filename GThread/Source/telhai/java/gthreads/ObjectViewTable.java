package telhai.java.gthreads;

import java.util.*;				// For vectors
import java.awt.*;				// For basic graphical functions
import javax.swing.*;			// For advanced graphical functions
import javax.swing.table.*;		// For table functions
import javax.swing.border.*;	// For border functions
import java.lang.reflect.*;		// For reflection

/** This class provides the ability to view all fields of a given object. The component includes two sub-components:
  * a JLabel which displays the name of the object, and a JTable which displays the fields of the object, their names,
  * types and values. The table is placesd inside a JScrollPane for automatic scrolling.
  *
  * A single instance of ObjectViewTable monitors a single Object. The object is received upon construction and cannot
  * be changed. However, the values of the fields are retrieved every time the component is repainted, so it can be used
  * to track changes of the object at run-time.
  *
  * By default, an ObjectViewTable only monitors the declared fields of the object, not the fields which it has inherited
  * from its ancestors. However, it is possible to specify upon construction that the component should also monitor
  * inherited fields.
  * 
  * @author Alex Frid alex.frid@gmail.com; Dima Ruinski
  */
public class ObjectViewTable extends JPanel
{
	/** The object which is monitored by this ObjectViewTable */
	protected Object myObject;
	/** The label which displays the name of the object */
	protected JLabel title;
	/** The table which displays the fields of the object */
	protected JTable fieldtable;
	/** The scroll pane which holds the table */
	protected JScrollPane spane;
	/** Holds the fields of the object */
	protected Field[] fields;
	/** A flag which indicates whether inherited fields should be monitored */
	protected boolean viewAncestors;

	/** Default width of the component */
	protected final int DEFAULT_WIDTH = 400;
	/** Default height of the component */
	protected final int DEFAULT_HEIGHT = 150;
	/** The height of the label */
	protected final int LABEL_HEIGHT = 20;
	/** The distance from the border */
	protected final int BORDER_OFFSET = 5;
	/** The color of the border */
	protected final Color BORDER_COLOR = Color.BLACK;
	/** The names of the columns of the table */
	protected final String[] COLUMN_NAMES = {"Name","Type","Value"};

	/** Constructs an ObjectViewTable with no object to monitor. This constructor exists for compatibility only, as there
	  * is no use to an ObjectViewTable without an object.
	  */
	public ObjectViewTable ()
	{
		myObject = null;
		
		setPreferredSize(new Dimension(DEFAULT_WIDTH,LABEL_HEIGHT));
		setBorder(new MatteBorder(1,1,1,1,BORDER_COLOR));
	}

	/** Constructs an ObjectViewTable which monitors the given object.
	  * @param obj the object to monitor.
	  */
	public ObjectViewTable (Object obj)
	{
		myObject = obj;

		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
		setBorder(new MatteBorder(1,1,1,1,BORDER_COLOR));

		viewAncestors = false;		// By default doesn't show inherited fields
	
		initializeTable();			// Internal function to initialize the component
	}

	/** Constructs an ObjectViewTable which monitors the given object.
	  * @param obj the object to monitor.
	  * @param ancestors indicates whether inherited fields should be viewed or not.
	  */
	public ObjectViewTable (Object obj, boolean ancestors)
	{
		myObject = obj;

		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
		setBorder(new MatteBorder(1,1,1,1,BORDER_COLOR));
	
		viewAncestors = ancestors;
		
		initializeTable();			// Internal function to initialize the component
	}

	/** Constructs an ObjectViewTable with a given dimension, which monitors the given object.
	  * @param obj the object to monitor.
	  * @param dim the desired dimension of the component.
	  * @param ancestors indicates whether inherited fields should be viewed or not.
	  */
	public ObjectViewTable (Object obj, Dimension dim, boolean ancestors)
	{
		myObject = obj;

		if (dim.height<LABEL_HEIGHT)	// Doesn't allow height smaller than the label height
			setPreferredSize(new Dimension(dim.width,LABEL_HEIGHT));
		else
			setPreferredSize(dim);

		setBorder(new MatteBorder(1,1,1,1,BORDER_COLOR));

		viewAncestors = ancestors;
		
		initializeTable();			// Internal function to initialize the component
	}
		
	/** Initializes the component, by adding the label and tabel components and retrieving the fields of the object,
	  * which needs to be monitored.
	  */
	protected void initializeTable()
	{
		if (myObject==null)		// If the object is NULL, only add the label to display "NO OBJECT"
		{
			title = new JLabel ("NO OBJECT",SwingConstants.CENTER);
			title.setSize(DEFAULT_WIDTH,LABEL_HEIGHT);
			add(title,BorderLayout.NORTH);
			return;
		}
	
		title = new JLabel (myObject.toString(),SwingConstants.CENTER);	// Display the string representation of the object
		
		fields = getMyFields();							// Internal function to retrieve the fields of the object
		AccessibleObject.setAccessible(fields,true);	// Allow viewing private and protected fields

		TableModel tmodel = new AbstractTableModel()		// Create a table model to display the table
		{
			public String getColumnName (int col) {return COLUMN_NAMES[col];}	// Return the column names
			public int getRowCount() {return fields.length;}					// Return the number of rows
			public int getColumnCount() {return COLUMN_NAMES.length;}			// Return the number of columns
			public Object getValueAt (int row, int col)							// Return the value of a given cell
			{
				Object val = null;
				switch (col)
				{
					case 0: val = fields[row].getName();			// Column 0 - names of fields
							break;
					case 1: val = fields[row].getType().getName();	// Column 1 - types of fields
							break;
					case 2: try {val = fields[row].get(myObject);}	// Column 2 - values of fields
							catch (IllegalAccessException e) {}		// This exception will never occur here
							break;									// But the compiler demands trying to catch it
				}
				return val;
			}
			public boolean isCellEditable(int row, int col) {return false;}		// The table is not editable
			public void setValueAt (Object value, int row, int col) {}			// No implementation for setValueAt()
		};

		fieldtable = new JTable(fields.length,COLUMN_NAMES.length);		// Create a table of the proper size
		fieldtable.setModel(tmodel);									// Apply the table model
		
		spane = new JScrollPane(fieldtable);		// Add table to a scrollpane and set the needed size
		spane.setPreferredSize(new Dimension(getPreferredSize().width-2*BORDER_OFFSET,
														getPreferredSize().height-2*LABEL_HEIGHT));
		
		add(title);		// Add label to component
		add(spane);		// Add scrollpane with table to component

		validate();		// Validate component - set the bounds of the sub-components to the appropriate values
	}

	/** Returns an array of all the fields of the object which need to be monitored. If viewAncestors is false (default),
	  * only the declared fields are returned. Otherwise, all the declared fields of the object's class and all its 
	  * superclasses are returned.
	  */
	protected Field[] getMyFields()
	{
		Field[] currentFields;				// The fields of the current class
		Class currentClass;					// The current class
		Vector fieldVector = new Vector();	// Temporary vector to hold the fields
		int i;
		
		currentClass = myObject.getClass();					// The class of this object
		currentFields = currentClass.getDeclaredFields();	// All the declared fields of this class

		if (viewAncestors)							// If inherited fields are to be monitored
		{
			for (i=0;i<currentFields.length;++i)
				fieldVector.add(currentFields[i]);	// Add all fields to vector

			while ((currentClass=currentClass.getSuperclass())!=null)	// Go up the inheritance tree until class Object
			{
				currentFields = currentClass.getDeclaredFields();	// Get the fields of this ancestor class
				for (i=0;i<currentFields.length;++i)
					fieldVector.add(currentFields[i]);				// Add the fields to the vector
			}
		
			currentFields = new Field[fieldVector.size()];

			for (i=0;i<fieldVector.size();++i)
				currentFields[i] = (Field)(fieldVector.elementAt(i));	// Convert the vector back to an array
		}

		return currentFields;
	}

	/** Validates this component by ensuring that both of its subcomponents (the JLabel and JScrollpane) are placed and
	  * sized as necessary. The label is places at the top of the component, and the scrollpane which contains the table
	  * is placed underneath it.
	  */
	public void validate()
	{
		super.validate();

		title.setBounds(BORDER_OFFSET,0,getPreferredSize().width-BORDER_OFFSET,LABEL_HEIGHT);
		
		if (spane!=null)
			spane.setBounds(BORDER_OFFSET,LABEL_HEIGHT,spane.getPreferredSize().width,spane.getPreferredSize().height);
	}

	/** Returns a string representation of this ObjectViewTable. The string representation includes the string
	  * representation of the object monitored and the name of its class.
	  */
	public String toString()
	{
		if (myObject==null)
			return ("Object View Table [ NULL ]");
		return ("Object View Table [ Object: "+myObject.toString()+" ; Class: "+myObject.getClass().getName()+" ]");
	}
}