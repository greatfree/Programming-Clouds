package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootBroadcastReaderSource;
import org.greatfree.testing.message.DNAnycastRequest;

/*
 * The source contains all of the arguments to create the instance of anycast reader, AnycastReader. It is used by the resource pool that manages the instances of AnycastReader. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastReaderSource extends RootBroadcastReaderSource<String, DNAnycastRequest, AnycastRequestCreator>
{

	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public AnycastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, AnycastRequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}
}
