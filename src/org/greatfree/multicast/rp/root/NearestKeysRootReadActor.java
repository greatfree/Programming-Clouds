package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 10/20/2018, Bing Li
class NearestKeysRootReadActor extends Async<NearestKeysMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public NearestKeysRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void perform(NearestKeysMulticastRequest message)
	{
		try
		{
			this.multicastor.nearestRead(message.getDataKeys(), message.getRequest());
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
