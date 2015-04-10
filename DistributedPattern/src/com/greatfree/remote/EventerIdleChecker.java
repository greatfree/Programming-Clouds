package com.greatfree.remote;

import java.util.TimerTask;

import com.greatfree.concurrency.CheckIdleable;

/*
 * The class works with AsyncRemoteEventer to check whether an instance of Eventer is idle long enough so that it should be disposed. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class EventerIdleChecker<T extends CheckIdleable> extends TimerTask
{
	// The resource to be checked must implement the interface of CheckIdleable. 11/20/2014, Bing Li
	private T t;

	/*
	 * Initialize the checker. 11/20/2014, Bing Li
	 */
	public EventerIdleChecker(T t)
	{
		this.t = t;
	}

	/*
	 * Check the resource by calling back its corresponding method concurrently and periodically. 11/20/2014, Bing Li
	 */
	@Override
	public void run()
	{
		t.checkIdle();
	}
}
