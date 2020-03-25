package org.greatfree.multicast.child.abandoned;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * The source contains the arguments to initialize a new broadcastor to forward the request of BroadcastRequest. It is used by the resource pool. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
class ChildBroadcastReaderSource<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>> extends SubBroadcastReaderSource<Request, RequestCreator>
{

	public ChildBroadcastReaderSource(String localIPKey, FreeClientPool clientPool, int treeBranchCount, RequestCreator creator)
	{
		super(localIPKey, clientPool, treeBranchCount, creator);
	}

}
