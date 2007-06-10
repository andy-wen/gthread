package telhai.java.gthreads;

import java.io.*;

/** This class extends the basic Thread class to provide additional functionality for Java threads.
  * First and foremost, there is a method to check whether a thread is currently awake or sleeping. Also, each ThreadPlus
  * object comes with a designated pipe for output (PipedOutputStream). It is done so that all the output which a thread
  * produces can be channeled into one stream, which can be monitored by anyone who desires it.
  */
public class ThreadPlus extends Thread
{
	/** Indicates whether the thread is currently awake */
	protected boolean awake;
	/** The designated output pipe of this thread */
	protected PipedOutputStream outStream;
	/** Wraps the output pipe to allow more convenient writing */
	protected PrintWriter out;
	
	/** Allocates a new ThreadPlus object and initializes its output stream. */
	public ThreadPlus()
	{
		super();
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object to run a given Runnable object.
	  * @param target the object whose run() method is called
	  */
	public ThreadPlus (Runnable target)
	{
		super(target);
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object with the given target and name and initializes its output stream.
	  * @param target the object whose run() method is called.
	  * @param name the name of this ThreadPlus.
	  */
	public ThreadPlus (Runnable target, String name)
	{
		super(target,name);
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object with the given name and initializes its output stream.
	  * @param name the name of this ThreadPlus.
	  */
	public ThreadPlus (String name)
	{
		super(name);
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object with a given Runnable object and associates it to a given thread group.
	  * @param target the object whose run() method is called.
	  * @param group the thread group.
	  */
	public ThreadPlus (ThreadGroup group, Runnable target)
	{
		super(group,target);
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object with a given name and associates it to a given thread group.
	  * @param name the name of this ThreadPlus.
	  * @param group the thread group.
	  */
	public ThreadPlus (ThreadGroup group, String name)
	{
		super(group,name);
		awake=false;
		initOutStream();
	}

	/** Allocates a new ThreadPlus object with a given name, target and group, and initializes its output stream.
	  * @param name the name of this ThreadPlus.
	  * @param target the object whose run() method is called.
	  * @param group the thread group.
	  */
	public ThreadPlus (ThreadGroup group, Runnable target, String name)
	{
		super(group,target,name);
		awake=false;
		initOutStream();
	}

	/** Checks whether the thread is awake. A thread is considered awake if it has been started and has not been
	  * suspended by a call to sleep() or yield().
	  * @return true if the thread is awake, false otherwise.
	  */
	public boolean isAwake()
	{
		return awake;
	}

	/** Starts this ThreadPlus object. This method sets the awake flag to true and calls the start() method of the
	  * Thread class.
	  */
	public void start()
	{
		awake=true;
		super.start();
	}

	/** Causes the currently executing thread to sleep (cease execution) for the specified number of milliseconds
	  * plus the specified number of nanoseconds. The thread does not lose ownership of any monitors. This method sets
	  * the awake flag to false before calling to Thread.sleep() and sets it back to true when Thread.sleep() returns.
	  * @param millis the length of time to sleep in milliseconds.
	  * @throws InterruptedException if another thread has interrupted the current thread.
	  * The interrupted status of the current thread is cleared when this exception is thrown.
	  */
	public static void sleep (long millis) throws InterruptedException
	{
		((ThreadPlus)currentThread()).awake=false;
		Thread.sleep(millis);
		((ThreadPlus)currentThread()).awake=true;
	}

	/** Causes the currently executing thread to sleep (cease execution) for the specified number of milliseconds
	  * plus the specified number of nanoseconds. The thread does not lose ownership of any monitors. This method sets
	  * the awake flag to false before calling to Thread.sleep() and sets it back to true when Thread.sleep() returns.
	  * @param millis the length of time to sleep in milliseconds.
	  * @param nanos 0-999999 additional nanoseconds to sleep.
	  * @throws InterruptedException if another thread has interrupted the current thread.
	  * The interrupted status of the current thread is cleared when this exception is thrown.
	  * @throws IllegalArgumentException if the value of millis is negative or the value of nanos is not in the range
	  * 0-999999.
	  */
	public static void sleep (long millis, int nanos) throws InterruptedException
	{
		((ThreadPlus)currentThread()).awake=false;
		Thread.sleep(millis,nanos);
		((ThreadPlus)currentThread()).awake=true;
	}

	/** Causes the currently executing thread object to temporarily pause and allow other threads to execute. This method
	  * sets the awake flag to false before calling to Thread.yield() and sets it back to true when Thread.yield()
	  * returns.
	  */
	public static void yield()
	{
		((ThreadPlus)currentThread()).awake=false;
		Thread.yield();
		((ThreadPlus)currentThread()).awake=true;
	}

	/** Returns a string representation of this ThreadPlus object, including its name, priority, and thread group.
	  */
	public String toString()
	{
		return "ThreadPlus["+getName()+","+getPriority()+","+getThreadGroup().getName()+"]";
	}

	/** Returns the output stream of this thread. Later, a piped input stream can be connected to this output stream,
	  * to allow the output of the thread be received elsewhere.
	  * @return the PipedOutputStream object associated with this ThreadPlus.
	  */
	public PipedOutputStream getOutputStream()
	{
		return outStream;
	}

	/** Prints a string to the designated pipe of this thread. The output buffer will be automatically flushed.
	  */
	public void print (String s)
	{
		out.print(s);
		out.flush();
	}

	/** Prints a string to the designated pipe of this thread and places a line terminator after it. The output buffer
	  * will be automatically flushed.
	  */
	public void println (String s)
	{
		out.println(s);
		out.flush();
	}

	/** Initializes the designated pipe of this ThreadPlus object. First a new PipedOutputStream is created, and then
	  * a PrintWriter is wrapped around it.
	  */
	protected void initOutStream()
	{
		outStream = new PipedOutputStream();
		out = new PrintWriter(new OutputStreamWriter(outStream));
	}

	/** Closes the output stream initialized by this thread.
	  */
	protected void finalize() throws Throwable
	{
		try
		{
			if (outStream!=null)
				outStream.close();
			if (out!=null)
				out.close();
		}
		finally {super.finalize();}
	}
}