package org.greatfree.testing.client.abandoned;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.testing.client.ClientListener;

/*
 * The class is abandoned. 05/19/2018, Bing Li
 */

/*
 * This is an implementation of RunDisposable interface to shutdown ClientListener. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
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
