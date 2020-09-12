package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/15/2018, Bing Li
// class SizeRootReadActor extends Async<SizeMulticastRequest>
class SizeRootReadActor extends AsyncMulticastor<SizeMulticastRequest>
{
	public SizeRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(SizeMulticastRequest request)
	{
		try
		{
			super.getMulticastor().readWithNResponses(request.getRequest(), request.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
