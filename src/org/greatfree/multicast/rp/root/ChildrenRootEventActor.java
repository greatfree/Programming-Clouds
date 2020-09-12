package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.ChildrenMulticastNotification;

// Created: 10/15/2018, Bing Li
class ChildrenRootEventActor extends Async<ChildrenMulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(ChildrenMulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification.getNotification(), notification.getChildrenKeys());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
