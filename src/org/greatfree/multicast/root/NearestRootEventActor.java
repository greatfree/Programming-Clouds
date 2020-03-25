package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/10/2018, Bing Li
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
