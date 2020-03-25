package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/10/2018, Bing Li
class ChildRootEventActor extends Async<ChildMulticastMessage>
{
	private RootSyncMulticastor multicastor;
	
	public ChildRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(ChildMulticastMessage notification)
	{
		try
		{
			this.multicastor.notify(notification.getMessage(), notification.getChildKey());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
