package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Async;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 10/20/2018, Bing Li
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
