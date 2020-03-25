package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * The source contains all of the arguments to create the instance of unicast reader, UnicastReader. It is used by the resource pool that manages the instances of UnicastReader. 11/29/2014, Bing Li
 */

// Created: 05/07/2017, Bing Li
class RootUnicastReaderSource<Data extends Serializable, Request extends OldMulticastRequest, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> extends BaseBroadcastReaderSource<Data, Request, RequestCreator>
{

	public RootUnicastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}

}
