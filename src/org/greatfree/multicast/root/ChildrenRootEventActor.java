package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/10/2018, Bing Li
// class ChildrenRootEventActor extends Async<ChildrenMulticastMessage>
final class ChildrenRootEventActor extends AsyncMulticastor<ChildrenMulticastNotification>
{
	public ChildrenRootEventActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void notify(ChildrenMulticastNotification notification)
	{
		try
		{
			super.getMulticastor().notify(notification.getNotification(), notification.getChildrenKeys());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildrenMulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
