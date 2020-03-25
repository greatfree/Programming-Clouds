package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/10/2018, Bing Li
class ChildrenRootEventActor extends Async<ChildrenMulticastMessage>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(ChildrenMulticastMessage notification)
	{
		try
		{
			this.multicastor.notify(notification.getMessage(), notification.getChildrenKeys());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
