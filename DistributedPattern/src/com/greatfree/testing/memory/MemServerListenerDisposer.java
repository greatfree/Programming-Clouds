package com.greatfree.testing.memory;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of MemServerListener by invoking its method of shutdown(). It works with the thread container, Threader or Runner. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemServerListenerDisposer implements RunDisposable<MemServerListener>
{
	/*
	 * Dispose the instance of CrawlingListener. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MemServerListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of MemServerListener. The method does not make sense to MemServerListener. Just leave it here for the requirement of the interface, RunDisposable<MemServerListener>. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MemServerListener r, long time)
	{
		r.shutdown();
	}
}
