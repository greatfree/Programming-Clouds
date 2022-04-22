package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class NearestKeyRootReadActor extends Async<NearestKeyMulticastRequest>
final class NearestKeyRootReadActor extends AsyncMulticastor<NearestKeyMulticastRequest>
{
	public NearestKeyRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(NearestKeyMulticastRequest request)
	{
		try
		{
			super.getMulticastor().nearestRead(request.getDataKey(), request.getRequest());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(NearestKeyMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
