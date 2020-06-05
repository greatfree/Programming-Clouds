package ca.streaming.news.root;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.multicast.root.RootClient;

// Created: 03/31/2020, Bing Li
class NewsRootMulticastor
{
	private RootClient client;

	private NewsRootMulticastor()
	{
	}
	
	private static NewsRootMulticastor instance = new NewsRootMulticastor();
	
	public static NewsRootMulticastor STREAM()
	{
		if (instance == null)
		{
			instance = new NewsRootMulticastor();
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
