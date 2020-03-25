package org.greatfree.testing.server.abandoned;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.testing.server.ManServerListener;

/*
 * The class is responsible for disposing the instance of ManServerListener by invoking its method of shutdown(). 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ManServerListenerDisposer implements RunnerDisposable<ManServerListener>
{
	/*
	 * Dispose the instance of ManServerListener. 01/20/2016, Bing Li
	 */
	@Override
	public void dispose(ManServerListener r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * Dispose the instance of ManServerListener. The method does not make sense to ManServerListener. Just leave it here for the requirement of the interface, RunDisposable<ManServerListener>. 01/20/2016, Bing Li
	 */
	@Override
	public void dispose(ManServerListener r, long time) throws InterruptedException
	{
		r.dispose();
	}
}
