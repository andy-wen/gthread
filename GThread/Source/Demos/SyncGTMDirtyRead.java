package Demos;

// This is a simple demo program which demonstrates how the Graphical Thread Manager can monitor
// threads and their messages, as well as objects and their fields.
//
// In this program both the setNames() and the getNames() methods are synchronized, so the second
// thread will not enter getNames() before the first one leaves setNames(). That way, the second
// thread will retrieve the correct values.
//

import telhai.java.gthreads.*;
/**
 * 
 * @author Alex Frid alex.frid@gmail.com; Dima Ruinski
 *
 */
public class SyncGTMDirtyRead extends Object {
	private String fname;
	private String lname;

	public synchronized String getNames() {
		return lname + ", " + fname;
	}

	public synchronized void setNames(
				String firstName, 
				String lastName
			) {

		print("entering setNames()");
		
		fname = firstName;

		try { if (Thread.currentThread() instanceof ThreadPlus)	// Only ThreadPlus sleeps here!
					ThreadPlus.sleep(10000);					// (main won't sleep)
			} 
		catch ( InterruptedException x ) { }

		lname = lastName;
		print("leaving setNames() - " + lname + ", " + fname);
	}

	public static void print(String msg) {	// Print to the ThreadPlus pipe
		if (Thread.currentThread() instanceof ThreadPlus)	// only if it's really a ThreadPlus!
		{
			String threadName = ThreadPlus.currentThread().getName();
			System.out.println(threadName + ": " + msg);
			((ThreadPlus)(ThreadPlus.currentThread())).println(threadName + ": " + msg);
		}
		else				// main thread will just print to the screen
		{
			String threadName = Thread.currentThread().getName();
			System.out.println(threadName + ": " + msg);
		}
	}

	public static void main(String[] args) {
		final SyncGTMDirtyRead dr = new SyncGTMDirtyRead();
		dr.setNames("George", "Washington"); // initially 

		GraphicalThreadManager manager = new GraphicalThreadManager();

		Runnable runA = new Runnable() {
				public void run() {
					dr.setNames("Abe", "Lincoln");
				}
			};

		Runnable runB = new Runnable() {
				public void run() {
					print("getNames()=" + dr.getNames());
					try {ThreadPlus.sleep(2000);} catch (InterruptedException x) {}
				}			// Add short sleep to allow observations
			};

		ThreadPlus threadA = new ThreadPlus(runA, "threadA");
		ThreadPlus threadB = new ThreadPlus(runB, "threadB");

		// Adds both threads to the manager and starts it.
		// In the manager we will see the initial values of the fields, then we'll see how the
		// second thread waits for the first thread to leave setNames() and then gets the correct
		// values of the fields.
		// manager.setPixelsPerTick(2);	// If want to speed-up the timeline
		manager.setAutoReset(false);
		manager.addThread(threadA);
		manager.addThread(threadB);
		manager.addObject(dr);
		manager.start();

		try { Thread.sleep(3000); }		// Add short delay to see initial values
		catch ( InterruptedException x ) { }

		threadA.start();

		try { Thread.sleep(2000); }		// Delay before starting the second thread
		catch ( InterruptedException x ) { }

		threadB.start();
	}
}
