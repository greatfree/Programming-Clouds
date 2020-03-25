package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.multicast.root.NearestMulticastMessage;

// Created: 10/15/2018, Bing Li
class NearestRootEventActor extends Async<NearestMulticastMessage>
{
	private RootSyncMulticastor multicastor;
	
	public NearestRootEventActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(NearestMulticastMessage notification)
	{
		try
		{
			this.multicastor.nearestNotify(notification.getKey(), notification.getMessage());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
