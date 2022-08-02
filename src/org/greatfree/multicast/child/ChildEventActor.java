package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
final class ChildEventActor implements Notifier<MulticastNotification>
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
		catch (IOException | DistributedNodeFailedException e)
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
