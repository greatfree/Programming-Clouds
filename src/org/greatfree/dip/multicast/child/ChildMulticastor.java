package org.greatfree.dip.multicast.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.multicast.child.ChildClient;

// Created: 09/08/2018, Bing Li
public class ChildMulticastor
{
	private ChildClient client;
	
	private ChildMulticastor()
	{
	}
	
	private static ChildMulticastor instance = new ChildMulticastor();
	
	public static ChildMulticastor CHILD()
	{
		if (instance == null)
		{
			instance = new ChildMulticastor();
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
