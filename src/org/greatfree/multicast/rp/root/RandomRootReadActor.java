package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class RandomRootReadActor extends Async<RPMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public RandomRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(RPMulticastRequest message)
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

	/*
	@Override
	public void perform(RPMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
