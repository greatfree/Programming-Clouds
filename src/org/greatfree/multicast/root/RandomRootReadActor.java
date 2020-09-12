package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
// class RandomRootReadActor extends Async<MulticastRequest>
class RandomRootReadActor extends AsyncMulticastor<MulticastRequest>
{
	public RandomRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(MulticastRequest message)
	{
		try
		{
			super.getMulticastor().randomRead(message);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
