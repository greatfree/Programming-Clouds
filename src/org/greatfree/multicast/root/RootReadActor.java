package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/15/2018, Bing Li
// class RootReadActor extends Async<MulticastRequest>
class RootReadActor extends AsyncMulticastor<MulticastRequest>
{
	public RootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}
	
	@Override
	public void perform(MulticastRequest request)
	{
		try
		{
			super.getMulticastor().read(request);
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
