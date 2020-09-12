package org.greatfree.multicast.root;

import java.io.IOException;

import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/16/2018, Bing Li
// class ChildKeyRootReadActor extends Async<ChildKeyMulticastRequest>
class ChildKeyRootReadActor extends AsyncMulticastor<ChildKeyMulticastRequest>
{
	public ChildKeyRootReadActor(RootSyncMulticastor multicastor)
	{
		super(multicastor);
	}

	@Override
	public void perform(ChildKeyMulticastRequest message)
	{
		try
		{
			super.getMulticastor().read(message.getChildKey(), message.getRequest());
		}
		catch (IOException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
	}

}
