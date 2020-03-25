package org.greatfree.testing.memory;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastorSource;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;

/*
 * The source to create the instance of IsPublisherExistedChildAnycastor. It is used by the resource pool to use the anycastors efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedChildAnycastorSource extends ChildMulticastorSource<IsPublisherExistedAnycastRequest, IsPublisherExistedRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedChildAnycastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, IsPublisherExistedRequestCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}
}
