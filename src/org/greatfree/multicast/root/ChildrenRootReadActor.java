package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/15/2018, Bing Li
// class ChildrenRootReadActor extends Async<ChildrenMulticastRequest>
class ChildrenRootReadActor extends AsyncMulticastor<ChildrenMulticastRequest>
{
	public ChildrenRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(ChildrenMulticastRequest message)
	{
		try
		{
			super.getMulticastor().read(message.getChildrenKeys(), message.getRequest());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
