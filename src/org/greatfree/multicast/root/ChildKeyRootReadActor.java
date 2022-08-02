package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class ChildKeyRootReadActor extends Async<ChildKeyMulticastRequest>
final class ChildKeyRootReadActor extends AsyncMulticastor<ChildKeyMulticastRequest>
{
	public ChildKeyRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void notify(ChildKeyMulticastRequest request)
	{
		try
		{
			super.getMulticastor().read(request.getChildKey(), request.getRequest());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(ChildKeyMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
