package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/15/2018, Bing Li
// class ChildrenRootReadActor extends Async<ChildrenMulticastRequest>
final class ChildrenRootReadActor extends AsyncMulticastor<ChildrenMulticastRequest>
{
	public ChildrenRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void notify(ChildrenMulticastRequest request)
	{
		try
		{
			super.getMulticastor().read(request.getChildrenKeys(), request.getRequest());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildrenMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
