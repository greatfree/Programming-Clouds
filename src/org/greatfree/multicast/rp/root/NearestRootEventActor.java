package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.NearestMulticastNotification;

// Created: 10/15/2018, Bing Li
class NearestRootEventActor extends Async<NearestMulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public NearestRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(NearestMulticastNotification notification)
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

}
