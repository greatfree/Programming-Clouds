package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of MainBroadcastReader for the resource pool. 05/05/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
class RootBroadcastReaderCreator<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> implements HashCreatable<RootBroadcastReaderSource<Data, Request, RequestCreator>, RootBroadcastReader<Data, Request, Response, RequestCreator>>
{

	@Override
	public RootBroadcastReader<Data, Request, Response, RequestCreator> createResourceInstance(RootBroadcastReaderSource<Data, Request, RequestCreator> source)
	{
		return new RootBroadcastReader<Data, Request, Response, RequestCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator(), source.getWaitTime());
	}

}
