package com.greatfree.testing.client;

import com.greatfree.reuse.RunDisposable;

/*
 * This is an implementation of RunDisposable interface to shutdown ClientListener. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientListenerDisposer implements RunDisposable<ClientListener>
{
	@Override
	public void dispose(ClientListener r)
	{
		r.shutdown();
	}

	@Override
	public void dispose(ClientListener r, long time)
	{
		r.shutdown();
	}
}
