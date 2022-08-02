package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/15/2018, Bing Li
class RootReadActor implements Notifier<RPMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public RootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void notify(RPMulticastRequest request)
	{
		try
		{
			this.multicastor.read(request);
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
