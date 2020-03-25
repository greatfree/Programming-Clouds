package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;

// Created: 09/10/2018, Bing Li
class RootEventActor extends Async<MulticastMessage>
{
	private RootSyncMulticastor multicastor;
	
	public RootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(MulticastMessage notification)
	{
		try
		{
			this.multicastor.notify(notification);
		}
		catch (IOException	| DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
