package com.greatfree.testing.coordinator.admin;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of AdminServerListener by invoking its method of shutdown(). 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class AdminListenerDisposer implements RunDisposable<AdminListener>
{
	/*
	 * Dispose the instance of AdminServerListener. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(AdminListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of AdminServerListener. The method does not make sense to AdminServerListener. Just leave it here for the requirement of the interface, RunDisposable<AdminServerListener>. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(AdminListener r, long time)
	{
		r.shutdown();
	}
}
