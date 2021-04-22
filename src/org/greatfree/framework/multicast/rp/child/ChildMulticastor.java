package org.greatfree.framework.multicast.rp.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.multicast.rp.child.ChildClient;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 10/21/2018, Bing Li
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
	
	public void start(String localIPKey, FreeClientPool clientPool, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.client = new ChildClient(localIPKey, clientPool, treeBranchCount, waitTime, pool);
	}
	
//	public OldRendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.client.getRP();
	}
	
	public void notifyRP(IPAddress rpIP, MulticastResponse response) throws IOException, InterruptedException
	{
		this.client.notify(rpIP.getIP(), rpIP.getPort(), response);
	}

	public void notify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.notify(notification);
	}
	
	public void asyncNotify(MulticastNotification notification)
	{
		this.client.asynNotify(notification);
	}
	
	public void read(RPMulticastRequest request, IPAddress address) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		ChildPeer.CHILD().setParentIP(request.getRPAddress());
		if (request.getChildrenIPs() != UtilConfig.NO_IPS)
		{
			request.setAddress(address);
		}
		this.client.read(request);
	}
	
	public void asyncRead(RPMulticastRequest request, IPAddress address) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		ChildPeer.CHILD().setParentIP(request.getRPAddress());
		if (request.getChildrenIPs() != UtilConfig.NO_IPS)
		{
			this.client.getRP().setReceiverSize(request.getCollaboratorKey(), request.getChildrenIPs().size() + 1);
			request.setAddress(address);
		}
		this.client.asyncRead(request);
	}
}
