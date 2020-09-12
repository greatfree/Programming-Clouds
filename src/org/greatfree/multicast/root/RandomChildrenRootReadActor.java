package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/11/2020, Bing Li
class RandomChildrenRootReadActor extends AsyncMulticastor<RandomChildrenMulticastRequest>
{

	public RandomChildrenRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(RandomChildrenMulticastRequest message)
	{
		try
		{
			super.getMulticastor().readWithinNChildren(message.getRequest(), message.getChildrenSize());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
