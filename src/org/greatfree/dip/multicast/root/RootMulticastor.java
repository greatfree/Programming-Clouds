package org.greatfree.dip.multicast.root;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.dip.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.multicast.root.RootClient;
import org.greatfree.multicast.root.RootRendezvousPoint;

// Created: 08/26/2018, Bing Li
public class RootMulticastor
{
//	private RootEventer eventer;
	
//	private RootReader reader;
//	private RootReader<HelloWorldBroadcastResponse> helloBroadcastReader;
//	private RootReader<HelloWorldAnycastResponse> helloAnycastReader;
//	private RootReader<HelloWorldUnicastResponse> helloUnicastReader;
	
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
//		this.eventer.dispose();
		
//		this.reader.dispose();
//		this.helloAnycastReader.dispose();
//		this.helloUnicastReader.dispose();
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
//		this.eventer = new RootEventer(clientPool, rootBranchCount, treeBranchCount);
		
//		this.reader = new RootReader<HelloWorldBroadcastResponse>(clientPool, rootBranchCount, treeBranchCount, waitTime);
//		this.reader = new RootReader(clientPool, rootBranchCount, treeBranchCount, waitTime);
//		this.helloAnycastReader = new RootReader<HelloWorldAnycastResponse>(clientPool, rootBranchCount, treeBranchCount, waitTime);
//		this.helloUnicastReader = new RootReader<HelloWorldUnicastResponse>(clientPool, rootBranchCount, treeBranchCount, waitTime);
		this.client = new RootClient(clientPool, rootBranchCount, treeBranchCount, waitTime, pool);
	}

//	public RootReader<HelloWorldBroadcastResponse> getBroadcastReader()
//	public RootReader getMulticastReader()
	public RootRendezvousPoint getRP()
	{
		return this.client.getRP();
	}

	/*
	public RootReader<HelloWorldAnycastResponse> getAnycastReader()
	{
		return this.helloAnycastReader;
	}
	
	public RootReader<HelloWorldUnicastResponse> getUnicastReader()
	{
		return this.helloUnicastReader;
	}
	*/
	
	public void broadcastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
//		this.eventer.disseminate(notification);
		this.client.broadcastNotify(notification);
	}
	
	public void anycastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
//		this.eventer.disseminate(notification);
		this.client.anycastNotify(notification);
	}
	
	public void unicastNotify(MulticastMessage notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException
	{
//		this.eventer.randomDisseminate(notification);
		this.client.unicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
//		return this.reader.broadcast(request);
		return this.client.broadcastRead(request);
	}
	
	/*
	 * The method is better since it hides the details to convert the list of parent objects to the list of child objects. 03/13/2020, Bing Li
	 */
	public List<HelloWorldBroadcastResponse> broadRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.broadcastRead(request, HelloWorldBroadcastResponse.class);
	}
	
	/*
	public List<HelloWorldBroadcastResponse> broadcastRead(ClusterBroadcastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.reader.broadcast(request);
	}
	*/

	/*
	 * Anycast requests
	 * 
	 * n: the least count of responses
	 * 
	 */
	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
//		return this.reader.anycast(request, n);
		return this.client.anycastRead(request, n);
	}

	public List<HelloWorldAnycastResponse> anyRead(MulticastRequest request, int n) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
		return this.client.anycastRead(request, n, HelloWorldAnycastResponse.class);
	}

	public List<MulticastResponse> unicastRead(MulticastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
//		return this.reader.unicast(request);
		return this.client.unicastRead(request);
	}

	public List<HelloWorldUnicastResponse> uniRead(MulticastRequest request) throws InstantiationException, IllegalAccessException, InterruptedException, IOException, DistributedNodeFailedException
	{
		return this.client.unicastRead(request, HelloWorldUnicastResponse.class);
	}
}
