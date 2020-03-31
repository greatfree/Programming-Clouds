package org.greatfree.testing.server;

import org.greatfree.reuse.RunnerDisposable;

/*
 * The class is responsible for disposing the instance of MyServerListener by invoking its method of shutdown(). 09/20/2014, Bing Li
 */

// Created: 08/10/2014, Bing Li
class ServerListenerDisposer implements RunnerDisposable<CSServerListener>
{
	/*
	 * Dispose the instance of MyServerListener. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(CSServerListener r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * Dispose the instance of MyServerListener. The method does not make sense to MyServerListener. Just leave it here for the requirement of the interface, RunDisposable<MyServerListener>. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(CSServerListener r, long time) throws InterruptedException
	{
		r.dispose();
	}
}
