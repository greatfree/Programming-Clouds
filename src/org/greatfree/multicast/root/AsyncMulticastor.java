package org.greatfree.multicast.root;

import org.greatfree.concurrency.Async;

// Created: 09/11/2020, Bing Li
abstract class AsyncMulticastor<Message> extends Async<Message>
{
	private RootSyncMulticastor multicastor;

	public AsyncMulticastor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	public RootSyncMulticastor getMulticastor()
	{
		return this.multicastor;
	}
}
