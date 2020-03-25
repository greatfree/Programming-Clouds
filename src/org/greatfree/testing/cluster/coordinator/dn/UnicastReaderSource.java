package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootBroadcastReaderSource;
import org.greatfree.testing.message.UnicastRequest;

/*
 * The source contains all of the arguments to create the instance of unicast reader, UnicastReader. It is used by the resource pool that manages the instances of UnicastReader. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastReaderSource extends RootBroadcastReaderSource<String, UnicastRequest, UnicastRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public UnicastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, UnicastRequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}
}
