package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class ChildrenSizeRootReadActor extends Async<ChildrenSizeMulticastRequest>
final class ChildrenSizeRootReadActor extends AsyncMulticastor<ChildrenSizeMulticastRequest>
{
	public ChildrenSizeRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(ChildrenSizeMulticastRequest request)
	{
		try
		{
			super.getMulticastor().read(request.getChildrenKeys(), request.getRequest(), request.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildrenSizeMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
