package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class NearestKeysRootReadActor extends Async<NearestKeysMulticastRequest> 
class NearestKeysRootReadActor extends AsyncMulticastor<NearestKeysMulticastRequest> 
{
	public NearestKeysRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(NearestKeysMulticastRequest message)
	{
		try
		{
			super.getMulticastor().nearestRead(message.getDataKeys(), message.getRequest());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
