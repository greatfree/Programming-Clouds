package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
// class RootEventActor extends Async<MulticastMessage>
final class RootEventActor extends AsyncMulticastor<MulticastNotification>
{
	public RootEventActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(MulticastNotification notification)
	{
		try
		{
			super.getMulticastor().notify(notification);
		}
		catch (IOException	| DistributedNodeFailedException e)
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
