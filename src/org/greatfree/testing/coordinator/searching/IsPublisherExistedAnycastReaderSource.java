package org.greatfree.testing.coordinator.searching;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootAnycastReaderSource;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;

/*
 * The source contains all of the arguments to create the instance of anycast reader, IsPublisherExistedAnycastReader. It is used by the resource pool that manages the instances of IsPublisherExistedAnycastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastReaderSource extends RootAnycastReaderSource<String, IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public IsPublisherExistedAnycastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, IsPublisherExistedAnycastRequestCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}
