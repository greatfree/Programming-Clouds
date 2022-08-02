package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
// class RandomRootReadActor extends Async<MulticastRequest>
final class RandomRootReadActor extends AsyncMulticastor<MulticastRequest>
{
	public RandomRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void notify(MulticastRequest request)
	{
		try
		{
			super.getMulticastor().randomRead(request);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(MulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
