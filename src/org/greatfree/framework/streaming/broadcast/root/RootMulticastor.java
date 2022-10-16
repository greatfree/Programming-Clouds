package org.greatfree.framework.streaming.broadcast.root;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.multicast.root.RootClient;

// Created: 03/18/2020, Bing Li
class RootMulticastor
{
	private RootClient client;

	private RootMulticastor()
	{
	}
	
	private static RootMulticastor instance = new RootMulticastor();
	
	public static RootMulticastor BROADCAST_STREAM()
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
	
	public void stop() throws IOException, InterruptedException, ClassNotFoundException
	{
		this.client.close();
	}

	public void start(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.client = new RootClient(clientPool, rootBranchCount, treeBranchCount, waitTime, pool);
	}
	
	public String getRandomChildKey()
	{
		return this.client.getRandomChildKey();
	}

	public void broadcastNotify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}
}
