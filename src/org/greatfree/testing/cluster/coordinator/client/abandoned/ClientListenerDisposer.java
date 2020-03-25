package org.greatfree.testing.cluster.coordinator.client.abandoned;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.testing.cluster.coordinator.client.ClientListener;

/*
 * The class is responsible for disposing the instance of CrawlListener by invoking its method of shutdown(). 11/25/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientListenerDisposer implements RunnerDisposable<ClientListener>
{

	@Override
	public void dispose(ClientListener r) throws InterruptedException
	{
		r.dispose();
	}

	@Override
	public void dispose(ClientListener r, long time) throws InterruptedException
	{
		r.dispose();
	}

}
