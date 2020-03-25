package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
class ChildrenSizeRootReadActor extends Async<ChildrenSizeMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenSizeRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(ChildrenSizeMulticastRequest message)
	{
		try
		{
			this.multicastor.read(message.getChildrenKeys(), message.getRequest(), message.getChildrenSize());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
