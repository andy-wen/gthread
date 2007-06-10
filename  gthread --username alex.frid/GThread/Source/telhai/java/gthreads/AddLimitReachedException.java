package telhai.java.gthreads;

/** Thrown by addThread() or addObject() methods of GraphicalThreadManager when trying to add threads or objects to it
  * beyond the internal limit.
  * @see GraphicalThreadManager  
  * @see GraphicalThreadManager#addThread
  * @see GraphicalThreadManager#addObject
  */
public class AddLimitReachedException extends RuntimeException
{
	/** Constructs an AddLimitReachedException with null as its error message string.
	  */
	public AddLimitReachedException()
	{
		super();
	}

	/** Constructs a NoSuchElementException, saving a reference to the error message string s for later retrieval by the
	  * getMessage() method.
	  * @param s the detail message.
	  */
	public AddLimitReachedException(String s)
	{
		super(s);
	}
}
