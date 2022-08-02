package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 10/20/2018, Bing Li
class NearestKeysRootReadActor implements Notifier<NearestKeysMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public NearestKeysRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void notify(NearestKeysMulticastRequest message)
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
