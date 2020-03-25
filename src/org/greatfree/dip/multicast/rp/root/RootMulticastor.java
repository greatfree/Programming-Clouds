package org.greatfree.dip.multicast.rp.root;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.multicast.rp.root.RootClient;

// Created: 10/21/2018, Bing Li
public class RootMulticastor
{
	private RootClient client;
	
	private RootMulticastor()
	{
	}
	
	private static RootMulticastor instance = new RootMulticastor();
	
	public static RootMulticastor ROOT()
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

	/*
	 * Start the root client
	 * 
	 * clientPool: the TCP client management mechanism
	 * 
	 * rootBranchCount: the immediate connected children count of the root
	 * 
	 * treeBranchCount: the branch count of multicasting tree except rootBranchCount
	 * 
	 */
	public void start(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.client = new RootClient(clientPool, rootBranchCount, treeBranchCount, waitTime, pool);
	}
	
//	public RendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.client.getRP();
	}
	
	public void broadcastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.broadcastNotify(notification);
	}
	
	public void anycastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.anycastNotify(notification);
	}
	
	public void unicastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.client.unicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(RPMulticastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
		return this.client.broadcastRead(request);
	}

	/*
	 * Anycast requests
	 * 
	 * n: the least count of responses
	 * 
	 */
	public List<MulticastResponse> anycastRead(RPMulticastRequest request, int n) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
		return this.client.anycastRead(request, n);
	}
	
	public List<MulticastResponse> unicastRead(RPMulticastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
		return this.client.unicastRead(request);
	}
	
}
