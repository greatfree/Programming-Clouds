package org.greatfree.testing.cluster.coordinator.dn.abandoned;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.testing.cluster.coordinator.dn.DNListener;

/*
 * The class is abandoned. 05/19/2018, Bing Li
 */

/*
 * The class is responsible for disposing the instance of DNListener by invoking its method of shutdown(). 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNListenerDisposer implements RunnerDisposable<DNListener>
{
	/*
	 * Dispose the instance of DNListener. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(DNListener r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * Dispose the instance of DNListener. The method does not make sense to DNListener. Just leave it here for the requirement of the interface, RunDisposable<DNListener>. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(DNListener r, long time) throws InterruptedException
	{
		r.dispose();
	}

}
