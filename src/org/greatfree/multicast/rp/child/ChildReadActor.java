package org.greatfree.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/13/2018, Bing Li
class ChildReadActor implements Notifier<RPMulticastRequest>
{
	private ChildSyncMulticastor multicastor;

	public ChildReadActor(ChildSyncMulticastor multicastor)
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
		catch (IOException | DistributedNodeFailedException | InterruptedException e)
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
