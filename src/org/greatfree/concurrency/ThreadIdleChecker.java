package org.greatfree.concurrency;

import java.util.TimerTask;

/*
 * This is a callback thread that runs periodically to call the idle checking method of the thread being monitored. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class ThreadIdleChecker<ThreadInstance extends CheckIdleable> extends TimerTask
{
	// Declare an instance of thread, which must implement the interface of CheckIdleable. 11/04/2014, Bing Li
	private ThreadInstance thread;

	/*
	 * A thread being monitored is input by the initialization. 11/04/2014, Bing Li
	 */
	public ThreadIdleChecker(ThreadInstance thread)
	{
		this.thread = thread;
	}

	// Check the thread idle state periodically. 11/04/2014, Bing Li
	@Override
	public void run()
	{
		try
		{
			thread.checkIdle();
		}
		catch (InterruptedException e)
		{
			// To save CPU cycles through closing the threads, it is not necessary to display the exception. 06/13/2019, Bing Li 
			e.printStackTrace();
		}
	}
}
