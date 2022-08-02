package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/10/2018, Bing Li
final class ChildReadActor implements Notifier<MulticastRequest>
{
	private ChildSyncMulticastor multicastor;
	
	public ChildReadActor(ChildSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void notify(MulticastRequest request)
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
	public void perform(MulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
