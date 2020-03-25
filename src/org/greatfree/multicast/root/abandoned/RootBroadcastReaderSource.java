package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * The source contains all of the arguments to create the instance of broadcast reader, MainBroadcastReader. It is used by the resource pool that manages the instances of MainBroadcastReader. 05/05/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
class RootBroadcastReaderSource<Data extends Serializable, Request extends OldMulticastRequest, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> extends BaseBroadcastReaderSource<Data, Request, RequestCreator>
{

	public RootBroadcastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}

}
