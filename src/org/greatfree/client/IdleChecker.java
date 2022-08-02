package org.greatfree.client;

import java.util.TimerTask;

import org.greatfree.concurrency.IdleCheckable;

/*
 * The class works with AsyncRemoteEventer to check whether an instance of Eventer is idle long enough so that it should be disposed. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class IdleChecker<T extends IdleCheckable> extends TimerTask
{
	// The resource to be checked must implement the interface of CheckIdleable. 11/20/2014, Bing Li
	private T t;

	/*
	 * Initialize the checker. 11/20/2014, Bing Li
	 */
	public IdleChecker(T t)
	{
		this.t = t;
	}

	/*
	 * Check the resource by calling back its corresponding method concurrently and periodically. 11/20/2014, Bing Li
	 */
	@Override
	public void run()
	{
		try
		{
			t.checkIdle();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
