package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.ChildMulticastNotification;

// Created: 10/15/2018, Bing Li
class ChildRootEventActor implements Notifier<ChildMulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public ChildRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void notify(ChildMulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification.getNotification(), notification.getChildKey());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildMulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
