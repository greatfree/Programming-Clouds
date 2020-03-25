package org.greatfree.testing.coordinator.admin.abandoned;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.testing.coordinator.admin.AdminListener;

/*
 * The class is responsible for disposing the instance of AdminServerListener by invoking its method of shutdown(). 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class AdminListenerDisposer implements RunnerDisposable<AdminListener>
{
	/*
	 * Dispose the instance of AdminServerListener. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(AdminListener r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * Dispose the instance of AdminServerListener. The method does not make sense to AdminServerListener. Just leave it here for the requirement of the interface, RunDisposable<AdminServerListener>. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(AdminListener r, long time) throws InterruptedException
	{
		r.dispose();
	}
}
