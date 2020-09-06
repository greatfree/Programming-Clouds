package org.greatfree.dsf.streaming.unicast.root;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.multicast.root.RootClient;

// Created: 03/22/2020, Bing Li
class RootMulticastor
{
	private RootClient client;

	private RootMulticastor()
	{
	}
	
	private static RootMulticastor instance = new RootMulticastor();
	
	public static RootMulticastor UNI_STREAM()
	{
		if (instance == null)
		{
			instance = new RootMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop() throws IOException, InterruptedException
	{
		this.client.close();
	}

	public void start(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.client = new RootClient(clientPool, rootBranchCount, treeBranchCount, waitTime, pool);
	}

	public void unicastNotify(String streamKey, MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.client.unicastNearestNotify(streamKey, notification);
	}

	public void broadcastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}
}
