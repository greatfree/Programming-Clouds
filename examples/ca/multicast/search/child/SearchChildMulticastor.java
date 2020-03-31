package ca.multicast.search.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.multicast.child.ChildClient;

// Created: 03/15/2020, Bing Li
class SearchChildMulticastor
{
	private ChildClient client;
	
	private SearchChildMulticastor()
	{
	}
	
	private static SearchChildMulticastor instance = new SearchChildMulticastor();
	
	public static SearchChildMulticastor CHILD()
	{
		if (instance == null)
		{
			instance = new SearchChildMulticastor();
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
	
	public void start(String localIPKey, FreeClientPool clientPool, int treeBranchCount, ThreadPool pool)
	{
		this.client = new ChildClient(localIPKey, clientPool, treeBranchCount, pool);
	}

	public void notify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.notify(notification);
	}
	
	public void asyncNotify(MulticastMessage notification)
	{
		this.client.asynNotify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.read(request);
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.client.asyncRead(request);
	}
}
