package telhai.java.gthreads.examples;

import telhai.java.gthreads.*;
import java.util.*;

/** This class is a simple extension of the telhai.java.gthreads.ThreadPlus class, meant to be used as an example for
  * demonstrating the functionality of the telhai.java.gthreads.GraphicalThreadManager class. This thread's run method
  * utilizes java.util.Random to generate random numbers. Each instance holds four numeric fields: integer, long, float
  * and double. The thread can continuously produce random numbers of all or some of the above types. Which types of
  * numbers will be produced depends on the key passed during construction. The run method will run loop infinitely
  * (until the thread is killed), causing to change approximately each 100ms.
  * @see telhai.java.gthreads.ThreadPlus
  */
public class RandThread extends ThreadPlus
{
	protected int i;
	protected long l;
	protected float f;
	protected double d;

	protected Random generator;

	/** Key value: don't generate anything */
	public static final int R_0 = 0;
	/** Key value: generate random int */
	public static final int R_I = 1;
	/** Key value: generate random long int */
	public static final int R_L = 2;
	/** Key value: generate random int,long */
	public static final int R_IL = 3;
	/** Key value: generate random float */
	public static final int R_F = 4;
	/** Key value: generate random int,float */
	public static final int R_IF = 5;
	/** Key value: generate random long,float */
	public static final int R_LF = 6;
	/** Key value: generate random int,long,float */
	public static final int R_ILF = 7;
	/** Key value: generate random double */
	public static final int R_D = 8;
	/** Key value: generate random int,double */
	public static final int R_ID = 9;
	/** Key value: generate random long,double */
	public static final int R_LD = 10;
	/** Key value: generate random int,long,double */
	public static final int R_ILD = 11;
	/** Key value: generate random float,double */
	public static final int R_FD = 12;
	/** Key value: generate random int,float,double */
	public static final int R_IFD = 13;
	/** Key value: generate random long,float,double */
	public static final int R_LFD = 14;
	/** Key value: generate random int,long,float,double */
	public static final int R_ILFD = 15;

	/** Holds the generator key as passed during construction */
	protected int generatorKey;

	/** Determines whether this thread should die */
	protected boolean die;

	/** Constructs a RandThread with the default key (generate all 4 types of numbers) and a default name. */
	public RandThread()
	{
		generatorKey = R_ILFD;
		setName ("Randomizer Thread");
		die = false;
	}

	/** Constructs a RandThread with given key. Invalid value will cause the default key to be used.
	  * @param key the key for the generator.
	  */
	public RandThread (int key)
	{
		generatorKey = (key<R_0 || key>R_ILFD) ? R_ILFD : key;
		setName ("Randomizer Thread");
		die = false;
	}

	/** Constructs a RandThread with the default key (generate all 4 types of numbers) and a given name.
	  * @param nam the name for this RandThread.
	  */
	public RandThread (String nam)
	{
		generatorKey = R_ILFD;
		setName(nam);
		die = false;
	}

	/** Constructs a RandThread with given key and name. Invalid key value will cause the default key to be used.
	  * @param key the key for the generator
	  * @param nam the name for this RandThread.
	  */
	public RandThread (int key, String nam)
	{
		generatorKey = (key<R_0 || key>R_ILFD) ? R_ILFD : key;
		setName(nam);
		die = false;
	}

	/** The run method for this thread which generates random numbers infinitely. Initially, all fields are set to
	  * zero. Then, some of them or all of them (according to the key value) will be randomized. The thread will sleep
	  * for 100ms between consecutive randomizations.
	  */
	public void run()
	{
		i = 0;
		l = 0;
		f = 0;
		d = 0;

		generator = new Random();

		while (!die)							// Run until die flag is raised
		{
			if ((generatorKey&1)!=0)
				i = generator.nextInt();
			if ((generatorKey&2)!=0)
				l = generator.nextLong();
			if ((generatorKey&4)!=0)
				f = generator.nextFloat();
			if ((generatorKey&8)!=0)
				d = generator.nextDouble();
			try {ThreadPlus.sleep(100);} catch (InterruptedException x) {}
		}
	}

	/** Kills this thread. Once this method is invoked, the thread will stop generating random numbers, and exit from
	  * the run() method.
	  */
	public void kill()
	{
		die = true;
	}
}