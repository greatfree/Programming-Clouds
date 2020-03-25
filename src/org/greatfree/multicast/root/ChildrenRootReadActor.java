package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/15/2018, Bing Li
class ChildrenRootReadActor extends Async<ChildrenMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}
	
	@Override
	public void perform(ChildrenMulticastRequest message)
	{
		try
		{
			this.multicastor.read(message.getChildrenKeys(), message.getRequest());
		}
		catch (DistributedNodeFailedException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
