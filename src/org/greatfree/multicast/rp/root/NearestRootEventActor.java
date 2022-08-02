package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.NearestMulticastNotification;

// Created: 10/15/2018, Bing Li
class NearestRootEventActor implements Notifier<NearestMulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public NearestRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void notify(NearestMulticastNotification notification)
	{
		try
		{
			this.multicastor.nearestNotify(notification.getKey(), notification.getNotification());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(NearestMulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
