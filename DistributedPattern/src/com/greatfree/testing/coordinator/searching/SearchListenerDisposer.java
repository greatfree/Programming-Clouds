package com.greatfree.testing.coordinator.searching;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of SearchListener by invoking its method of shutdown(). It is invoked by the runner to execute the server IOs concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchListenerDisposer implements RunDisposable<SearchListener>
{
	/*
	 * Dispose the instance of SearchListener. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(SearchListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of SearchListener. The method does not make sense to SearchListener. Just leave it here for the requirement of the interface, RunDisposable<SearchListener>. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(SearchListener r, long time)
	{
		r.shutdown();
	}
}
