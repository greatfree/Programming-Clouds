package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class NearestKeysRootReadActor extends Async<NearestKeysMulticastRequest> 
final class NearestKeysRootReadActor extends AsyncMulticastor<NearestKeysMulticastRequest> 
{
	public NearestKeysRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void notify(NearestKeysMulticastRequest request)
	{
		try
		{
			super.getMulticastor().nearestRead(request.getDataKeys(), request.getRequest());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	@Override
	public void perform(NearestKeysMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
