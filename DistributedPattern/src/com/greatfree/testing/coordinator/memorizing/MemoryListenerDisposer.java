package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of MemoryListener by invoking its method of shutdown(). 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryListenerDisposer implements RunDisposable<MemoryListener>
{
	/*
	 * Dispose the instance of MemoryListener. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MemoryListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of MemoryListener. The method does not make sense to MemoryListener. Just leave it here for the requirement of the interface, RunDisposable<MemoryListener>. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MemoryListener r, long time)
	{
		r.shutdown();
	}
}
