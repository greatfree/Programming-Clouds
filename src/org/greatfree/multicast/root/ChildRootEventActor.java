package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/10/2018, Bing Li
// class ChildRootEventActor extends Async<ChildMulticastMessage>
final class ChildRootEventActor extends AsyncMulticastor<ChildMulticastNotification>
{
	public ChildRootEventActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(ChildMulticastNotification notification)
	{
		try
		{
			super.getMulticastor().notify(notification.getNotification(), notification.getChildKey());
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
