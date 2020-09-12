package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
// class RandomRootEventActor extends Async<MulticastMessage>
class RandomRootEventActor extends AsyncMulticastor<MulticastNotification>
{
	public RandomRootEventActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(MulticastNotification notification)
	{
		try
		{
			super.getMulticastor().randomNotify(notification);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
