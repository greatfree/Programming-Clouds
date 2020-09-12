package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class NearestKeyRootReadActor extends Async<NearestKeyMulticastRequest>
class NearestKeyRootReadActor extends AsyncMulticastor<NearestKeyMulticastRequest>
{
	public NearestKeyRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(NearestKeyMulticastRequest message)
	{
		try
		{
			super.getMulticastor().nearestRead(message.getDataKey(), message.getRequest());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
