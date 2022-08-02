package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/11/2020, Bing Li
final class RandomChildrenRootReadActor extends AsyncMulticastor<RandomChildrenMulticastRequest>
{

	public RandomChildrenRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void notify(RandomChildrenMulticastRequest request)
	{
		try
		{
			super.getMulticastor().readWithinNChildren(request.getRequest(), request.getChildrenSize());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
		
	}

	/*
	@Override
	public void perform(RandomChildrenMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
