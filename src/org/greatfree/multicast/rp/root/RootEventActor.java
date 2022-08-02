package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 10/15/2018, Bing Li
class RootEventActor implements Notifier<MulticastNotification>
{
	private RootSyncMulticastor multicastor;
	
	public RootEventActor(RootSyncMulticastor multicastor)
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
