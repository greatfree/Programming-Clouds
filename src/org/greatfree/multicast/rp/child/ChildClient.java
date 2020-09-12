package org.greatfree.multicast.rp.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;

// Created: 10/13/2018, Bing Li
public class ChildClient
{
	private ChildEventer eventer;
	private ChildReader reader;
	private ChildSyncMulticastor multicastor;
	
	public ChildClient(String localIPKey, FreeClientPool clientPool, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.multicastor = new ChildSyncMulticastor(localIPKey, clientPool, treeBranchCount);
		this.eventer = new ChildEventer(this.multicastor, pool);
		this.reader = new ChildReader(this.multicastor, waitTime, pool);
	}

	public void close() throws IOException, InterruptedException
	{
		this.multicastor.dispose();
		this.eventer.dispose();
		this.reader.dispose();
	}
	
//	public OldRendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.reader.getRP();
	}
	
	public void notify(String ip, int port, MulticastResponse response) throws IOException, InterruptedException
	{
		this.eventer.notify(ip, port, response);
	}

	public void notify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}
	
	public void asynNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}

	public void read(RPMulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.reader.syncRead(request);
	}
	
	public void asyncRead(RPMulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.reader.asyncRead(request);
	}
}
