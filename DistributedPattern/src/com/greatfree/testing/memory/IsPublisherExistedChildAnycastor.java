package com.greatfree.testing.memory;

import com.greatfree.multicast.ChildMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;

/*
 * The anycast is implemented in the same way as an ordinary children multicastor, which transmits the anycast requests to its children. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedChildAnycastor extends ChildMulticastor<IsPublisherExistedAnycastRequest, IsPublisherExistedRequestCreator>
{
	/*
	 * Initialize the children anycastor. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedChildAnycastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, IsPublisherExistedRequestCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}
}
