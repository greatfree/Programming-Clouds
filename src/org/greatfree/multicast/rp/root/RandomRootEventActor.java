package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;

// Created: 10/15/2018, Bing Li
class RandomRootEventActor extends Async<MulticastMessage>
{
	private RootSyncMulticastor multicastor;
	
	public RandomRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(MulticastMessage notification)
	{
		try
		{
			this.multicastor.randomNotify(notification);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}
}