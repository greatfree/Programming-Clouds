package org.greatfree.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.Notifier;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 10/20/2018, Bing Li
class ChildrenSizeRootReadActor implements Notifier<ChildrenSizeMulticastRequest>
{
	private RootSyncMulticastor multicastor;
	
	public ChildrenSizeRootReadActor(RootSyncMulticastor multicastor)
	{
		this.multicastor = multicastor;
	}

	@Override
	public void notify(ChildrenSizeMulticastRequest message)
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

	/*
	@Override
	public void perform(ChildrenSizeMulticastRequest message, int cryptoOption)
	{
		// TODO Auto-generated method stub
		
	}
	*/

}
