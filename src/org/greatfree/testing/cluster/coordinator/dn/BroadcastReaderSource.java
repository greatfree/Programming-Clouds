package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootBroadcastReaderSource;
import org.greatfree.testing.message.DNBroadcastRequest;

/*
 * The source contains all of the arguments to create the instance of broadcast reader, DNBroadcastReader. It is used by the resource pool that manages the instances of DNBroadcastReader. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastReaderSource extends RootBroadcastReaderSource<String, DNBroadcastRequest, BroadcastRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public BroadcastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, BroadcastRequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}
}
