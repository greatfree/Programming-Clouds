package org.greatfree.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 10/14/2018, Bing Li
class ChildEventActor implements Notifier<MulticastNotification>
{
	private ChildSyncMulticastor multicastor;
	
	public ChildEventActor(ChildSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void notify(MulticastNotification notification)
	{
		try
		{
			this.multicastor.notify(notification);
		}
		catch (IOException | DistributedNodeFailedException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(MulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
