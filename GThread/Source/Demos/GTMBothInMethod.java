package Demos;

// This is a simple demo program which demonstrates how the Graphical Thread Manager can monitor
// threads and their messages.
//
// In this program two threads will enter the same method of class GTMBothInMethod
// simultaneously.
//

import telhai.java.gthreads.*;
/**
 *
 * @author Alex Frid alex.frid@gmail.com; Dima Ruinski
 */
public class GTMBothInMethod extends Object {
	private String objID;

	public GTMBothInMethod(String objID) {
		this.objID = objID;
	}

	public void doStuff(int val) {
		print("entering doStuff()");
		int num = val * 2 + objID.length();
		print("in doStuff() - local variable num=" + num);

		// slow things down to make observations
		// note that we call sleep() of ThreadPlus, not Thread
		try { ThreadPlus.sleep(5000); } catch ( InterruptedException x ) { }

		print("leaving doStuff()");
	}

	public void print(String msg) {
		threadPrint("objID=" + objID + " - " + msg);
	}

	public static void threadPrint(String msg) {	// Print to the ThreadPlus pipe
		String threadName = ThreadPlus.currentThread().getName();
		System.out.println(threadName + ": " + msg);
		((ThreadPlus)(ThreadPlus.currentThread())).println(threadName + ": " + msg);
	}

public static void main(String[] args) {
		final GTMBothInMethod bim = new GTMBothInMethod("obj1");

		GraphicalThreadManager manager = new GraphicalThreadManager();

		Runnable runA = new Runnable() {
				public void run() {
					bim.doStuff(3);
				}
			};

		Runnable runB = new Runnable() {
				public void run() {
					bim.doStuff(7);
				}
			};

		ThreadPlus threadA = new ThreadPlus(runA, "threadA");
		ThreadPlus threadB = new ThreadPlus(runB, "threadB");

		// Add both threads to manager and start it
		// In the manager we will see both threads sleeping inside dostuff() at the same time
		// and we will see their messages as they appear.
		// manager.setPixelsPerTick(2);	// If want to speed-up the timeline
		manager.setAutoReset(false);
		manager.addThread(threadA);
		manager.addThread(threadB);
		manager.start();

		threadA.start();

		try { Thread.sleep(2000); } catch ( InterruptedException x ) { }

		threadB.start();		// The second thread starts after a short delay
	}
}
