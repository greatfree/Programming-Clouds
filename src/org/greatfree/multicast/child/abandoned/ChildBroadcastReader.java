package org.greatfree.multicast.child.abandoned;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * The child broadcastor forwards the instance of Request to the local node's children. 11/29/2014, Bing Li
 */

// Created: 05/07/2017, Bing Li
class ChildBroadcastReader<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>> extends SubBroadcastReader<Request, RequestCreator>
{

	public ChildBroadcastReader(String localIPKey, FreeClientPool clientPool, int treeBranchCount, RequestCreator messageCreator)
	{
		super(localIPKey, clientPool, treeBranchCount, messageCreator);
	}

}
