package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/04/2018, Bing Li
public class ChildClient
{
	private ChildEventer eventer;
	private ChildReader reader;
	private ChildSyncMulticastor multicastor;

	public ChildClient(String localIPKey, FreeClientPool clientPool, int treeBranchCount, ThreadPool pool)
	{
		this.multicastor = new ChildSyncMulticastor(localIPKey, clientPool, treeBranchCount);
		this.eventer = new ChildEventer(this.multicastor, pool);
		this.reader = new ChildReader(this.multicastor, pool);
	}
	
	public void close() throws IOException, InterruptedException
	{
		this.eventer.dispose();
		this.reader.dispose();
		this.multicastor.dispose();
	}

	public void notify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}
	
	public void asynNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.reader.syncRead(request);
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.reader.asyncRead(request);
	}
}


