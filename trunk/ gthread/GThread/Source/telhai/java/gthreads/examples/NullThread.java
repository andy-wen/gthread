package telhai.java.gthreads.examples;

import telhai.java.gthreads.*;

/** This class is a simple extension of the telhai.java.gthreads.ThreadPlus class, meant to be used as an example for
  * demonstrating the functionality of the telhai.java.gthreads.GraphicalThreadManager class. This thread's run method
  * uses an empty loop which "counts" from 0 to a certain positive integer. After the loop finishes, the thread sleeps
  * for a predetermined amount of time, and starts over. Also, it is possible for the thread to print a message to the
  * standard piped output of telhai.java.gthreads.ThreadPlus to indicate that it finished the counting loop.
  *
  * The counting loop can run a given number of times or run "forever", in which case the thread can be stopped by
  * invoking its kill() method.
  * @see telhai.java.gthreads.ThreadPlus
  * @author Alex Frid alex.frid@gmail.com; Dima Ruinski
  */
public class NullThread extends ThreadPlus
{
	/** The default number of iterations for the counting loop (count up to 100,000,000) */
	public static final int DEFAULT_COUNT = 100000000;
	
	/** The default sleep period (2 seconds) */
	public static final int DEFAULT_SLEEP = 2000;

	/** The maximum allowed sleep period between iterations of the counting loop (30 seconds) */
	public static final int MAX_SLEEP = 30000;
	
	/** Holds the upper limit of the counting loop */
	protected int count;
	
	/** Holds the length of the sleep period */
	protected int delay;
	
	/** Holds the number of iterations (number of times the counting loop is executed) */
	protected int times;

	/** Determines whether the thread should sleep before counting for the first time */
	protected boolean initSleep;

	/** Determines whether the thread should print messages to the pipe after each iteration */
	protected boolean sendMessages;

	/** Determines whether this thread should die */
	protected boolean die;

	/** Internal function to set values */
	protected void setCountDelayTimes (int cnt, int slp, int tms)
	{
		count = (cnt>0) ? cnt : DEFAULT_COUNT;
		delay = (slp>0 && slp<MAX_SLEEP) ? slp : DEFAULT_SLEEP;
		times = (tms>=0) ? tms : 0;
	}

	/** Constructs a NullThread which counts to the default upper limit, sleeps a default period of time and runs
	  * forever, until it is killed.
	  */
	public NullThread()
	{
		setCountDelayTimes(DEFAULT_COUNT,DEFAULT_SLEEP,0);
		setName("Null Thread");
		die = false;
	}

	/** Constructs a NullThread which counts to the given upper limit, sleeps for the given period of time and runs
	  * forever, until it is killed.
	  * @param cnt the upper limit for the counting loop.
	  * @param slp the sleep period.
	  */
	public NullThread(int cnt, int slp)
	{
		setCountDelayTimes(cnt,slp,0);
		setName("Null Thread");
		die = false;
	}

	/** Constructs a NullThread which counts to the default upper limit, sleeps a default period of time and runs a
	  * given number of iterations, or until it's killed.
	  * @param tms the number of iterations (number of times to call the counting loop).
	  */
	public NullThread(int tms)
	{
		setCountDelayTimes(DEFAULT_COUNT,DEFAULT_SLEEP,tms);
		setName("Null Thread");
		die = false;
	}

	/** Constructs a NullThread which counts to the given upper limit, sleeps for the given period of time and runs a
	  * given number of iterations, or until it's killed.
	  * @param cnt the upper limit for the counting loop.
	  * @param slp the sleep period.
	  * @param tms the number of iterations (number of times to call the counting loop).
	  */
	public NullThread(int cnt, int slp, int tms)
	{
		setCountDelayTimes(cnt,slp,tms);
		setName("Null Thread");
		die = false;
	}

	/** Equivalent to NullThread(), but assigns a custom name to the thread. */
	public NullThread(String nam)
	{
		setCountDelayTimes(DEFAULT_COUNT,DEFAULT_SLEEP,0);
		setName(nam);
		die = false;
	}

	/** Equivalent to NullThread(int), but assigns a custom name to the thread. */
	public NullThread(int cnt, int slp, String nam)
	{
		setCountDelayTimes(cnt,slp,0);
		setName(nam);
		die = false;
	}

	/** Equivalent to NullThread(int,int), but assigns a custom name to the thread. */
	public NullThread(int tms, String nam)
	{
		setCountDelayTimes(DEFAULT_COUNT,DEFAULT_SLEEP,tms);
		setName(nam);
		die = false;
	}

	/** Equivalent to NullThread(int,int,int), but assigns a custom name to the thread. */
	public NullThread(int cnt, int slp, int tms, String nam)
	{
		setCountDelayTimes(cnt,slp,tms);
		setName(nam);
		die = false;
	}

	/** Determines whether the thread should sleep before starting the counting loop. This function has meaning only if
	  * the thread has not started yet.
	  * @param is if true, the thread will sleep before starting the counting.
	  */  
	public void setInitSleep (boolean is)
	{
		initSleep = is;
	}

	/** Determines whether the thread should print messages to the ThreadPlus pipe each time it finishes the counting
	  * loop.
	  * @param msg if true, the thread will print messages.
	  */
	public void setMessages (boolean msg)
	{
		sendMessages = msg;
	}

	/** The main run method which consists of the counting loop executing repeatedly. */
	public void run()
	{
		int i;

		if (initSleep)
			try {ThreadPlus.sleep(delay);} catch (InterruptedException x) {}

		if (times==0)
			for(;;)
			{
				for (i=0;i<count;++i);
				if (sendMessages)
					((ThreadPlus)(ThreadPlus.currentThread())).println("Iteration finished!");
				if (die)
					break;
				try {ThreadPlus.sleep(delay);} catch (InterruptedException x) {}
			}
		else
			for (int j=0;j<times;++j)
			{
				for (i=0;i<count;++i);
				if (sendMessages)
					((ThreadPlus)(ThreadPlus.currentThread())).println("Iteration " + (j+1) + " finished!");
				if (die)
					break;
				try {ThreadPlus.sleep(delay);} catch (InterruptedException x) {}
			}
	}

	/** Kills this thread. Once this method is invoked, the thread will complete the current counting loop, and exit
	  * from the run() method.
	  */
	public void kill()
	{
		die = true;
	}
}