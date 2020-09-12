package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
class ChildEventActor extends Async<MulticastNotification>
{
	private ChildSyncMulticastor multicastor;
	
	public ChildEventActor(ChildSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(MulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
