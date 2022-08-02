package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.ChildrenMulticastNotification;

// Created: 10/15/2018, Bing Li
class ChildrenRootEventActor implements Notifier<ChildrenMulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void notify(ChildrenMulticastNotification notification)
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

	/*
	@Override
	public void perform(ChildrenMulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
