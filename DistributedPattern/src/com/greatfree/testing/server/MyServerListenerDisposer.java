package com.greatfree.testing.server;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of MyServerListener by invoking its method of shutdown(). 09/20/2014, Bing Li
 */

// Created: 08/10/2014, Bing Li
public class MyServerListenerDisposer implements RunDisposable<MyServerListener>
{
	/*
	 * Dispose the instance of MyServerListener. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MyServerListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of MyServerListener. The method does not make sense to MyServerListener. Just leave it here for the requirement of the interface, RunDisposable<MyServerListener>. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MyServerListener r, long time)
	{
		r.shutdown();
	}
}
