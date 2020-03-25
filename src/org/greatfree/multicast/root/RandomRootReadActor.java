package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
class RandomRootReadActor extends Async<MulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public RandomRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(MulticastRequest message)
	{
		try
		{
			this.multicastor.randomRead(message);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
