package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The reader is derived from the MainRequestBroadcastor. It attempts to retrieve data in the way of broadcast among the cluster nodes. 05/05/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
class RootBroadcastReader<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>> extends BaseBroadcastReader<Data, Request, Response, RequestCreator>
{

	public RootBroadcastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator requestCreator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, waitTime);
	}

}
