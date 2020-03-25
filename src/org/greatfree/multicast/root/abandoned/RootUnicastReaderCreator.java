package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of UnicastRequestReader for the resource pool. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
class RootUnicastReaderCreator<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> implements HashCreatable<RootUnicastReaderSource<Data, Request, RequestCreator>, RootUnicastReader<Data, Request, Response, RequestCreator>>
{

	@Override
	public RootUnicastReader<Data, Request, Response, RequestCreator> createResourceInstance(RootUnicastReaderSource<Data, Request, RequestCreator> source)
	{
		return new RootUnicastReader<Data, Request, Response, RequestCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator(), source.getWaitTime());
	}

}
