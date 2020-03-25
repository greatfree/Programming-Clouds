package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/15/2018, Bing Li
class SizeRootReadActor extends Async<SizeMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public SizeRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(SizeMulticastRequest request)
	{
		try
		{
			this.multicastor.read(request.getRequest(), request.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
