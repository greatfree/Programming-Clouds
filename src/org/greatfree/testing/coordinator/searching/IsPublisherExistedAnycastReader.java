package org.greatfree.testing.coordinator.searching;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootRequestAnycastor;
import org.greatfree.testing.coordinator.CoorConfig;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The reader is derived from the RootRequestAnycastor. It attempts to retrieve data in the way of anycast among the cluster of memory nodes. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastReader extends RootRequestAnycastor<String, IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedAnycastRequestCreator>
{
	/*
	 * Initialize the anycastor. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, IsPublisherExistedAnycastRequestCreator requestCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, CoorConfig.ANYCAST_REQUEST_WAIT_TIME);
	}
}
