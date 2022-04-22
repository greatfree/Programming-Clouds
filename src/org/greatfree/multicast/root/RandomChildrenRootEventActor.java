package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/11/2020, Bing Li
class RandomChildrenRootEventActor extends AsyncMulticastor<RandomChildrenMulticastNotification>
{

	public RandomChildrenRootEventActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(RandomChildrenMulticastNotification notification)
	{
		try
		{
			super.getMulticastor().notifyWithinNChildren(notification.getNotification(), notification.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		
	}

	/*
	@Override
	public void perform(RandomChildrenMulticastNotification message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
