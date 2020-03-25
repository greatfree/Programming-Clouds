package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of AnycastRequestReader for the resource pool. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
class RootAnycastReaderCreator<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> implements HashCreatable<RootAnycastReaderSource<Data, Request, RequestCreator>, RootAnycastReader<Data, Request, Response, RequestCreator>>
{

	@Override
	public RootAnycastReader<Data, Request, Response, RequestCreator> createResourceInstance(RootAnycastReaderSource<Data, Request, RequestCreator> source)
	{
		return new RootAnycastReader<Data, Request, Response, RequestCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator(), source.getWaitTime());
	}

}
