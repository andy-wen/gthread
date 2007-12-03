package Demos;

// This is a simple demo program which demonstrates how the Graphical Thread Manager can monitor
// threads and their messages, as well as objects and their fields.
//
// In this program we will see how two thread sleep in the same method of class GTMCorruptWrite
// for different periods of time, resulting in incorrect values of the object fields.
//

import telhai.java.gthreads.*;
/**
 * 
 * @author Alex Frid alex.frid@gmail.com; Dima Ruinski
 *
 */
public class GTMCorruptWrite extends Object {
	private String fname;
	private String lname;

	public void setNames(String firstName, String lastName) {
		print("entering setNames()");
		fname = firstName;

		// A thread might be swapped out here, and may stay 
		// out for a varying amount of time. The different 
		// sleep times exaggerate this.
		if ( fname.length() < 5 ) {
			try { ThreadPlus.sleep(5000); } 
			catch ( InterruptedException x ) { }
		} else {
			try { ThreadPlus.sleep(10000); } 
			catch ( InterruptedException x ) { }
		}

		lname = lastName;

		print("leaving setNames() - " + lname + ", " + fname);
	}

	public static void print(String msg) {		// Print to the ThreadPlus pipe
		String threadName = ThreadPlus.currentThread().getName();
		System.out.println(threadName + ": " + msg);
		((ThreadPlus)(ThreadPlus.currentThread())).println(threadName + ": " + msg);
	}

	public static void main(String[] args) {
		final GTMCorruptWrite cw = new GTMCorruptWrite();

		GraphicalThreadManager manager = new GraphicalThreadManager();

		Runnable runA = new Runnable() {
				public void run() {
					cw.setNames("George", "Washington");
				}
			};

		Runnable runB = new Runnable() {
				public void run() {
					cw.setNames("Abe", "Lincoln");
				}
			};

		ThreadPlus threadA = new ThreadPlus(runA, "threadA");
		ThreadPlus threadB = new ThreadPlus(runB, "threadB");

		// Add both threads and the object to manager and start it.
		// In the manager we will see how the first thread goes to sleep before the second,
		// but wakes up after the second, causing the last name to receive the wrong value.
		// manager.setPixelsPerTick(2);	// If want to speed-up the timeline
		manager.setAutoReset(false);
		manager.addThread(threadA);
		manager.addThread(threadB);
		manager.addObject(cw);
		manager.start();

		threadA.start();

		try { Thread.sleep(2000); } 
		catch ( InterruptedException x ) { }

		threadB.start();
		
	}
}
