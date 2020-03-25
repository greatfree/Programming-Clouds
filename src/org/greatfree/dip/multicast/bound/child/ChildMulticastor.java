package org.greatfree.dip.multicast.bound.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.multicast.child.ChildClient;

// Created: 08/26/2018. Bing Li
public class ChildMulticastor
{
//	private ChildEventer eventer;
//	private ChildReader reader;
	
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
//		this.eventer.dispose();
//		this.reader.dispose();
		this.client.close();
	}
	
	public void start(String localIPKey, FreeClientPool clientPool, int treeBranchCount, ThreadPool pool)
	{
//		this.eventer = new ChildEventer(localIPKey, clientPool, treeBranchCount);
//		this.reader = new ChildReader(localIPKey, clientPool, treeBranchCount);
		this.client = new ChildClient(localIPKey, clientPool, treeBranchCount, pool);
	}

	public void notify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
//		this.eventer.disseminate(notification);
		this.client.notify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
//		this.reader.disseminate(request);
		this.client.read(request);
	}
	
}
