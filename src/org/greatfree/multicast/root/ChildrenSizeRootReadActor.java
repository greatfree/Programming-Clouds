package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class ChildrenSizeRootReadActor extends Async<ChildrenSizeMulticastRequest>
class ChildrenSizeRootReadActor extends AsyncMulticastor<ChildrenSizeMulticastRequest>
{
	public ChildrenSizeRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(ChildrenSizeMulticastRequest message)
	{
		try
		{
			super.getMulticastor().read(message.getChildrenKeys(), message.getRequest(), message.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
