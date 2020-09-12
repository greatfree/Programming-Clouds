package org.greatfree.multicast.rp.root;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.multicast.RendezvousPoint;
import org.greatfree.util.Tools;

// Created: 10/15/2018, Bing Li
public class RootClient
{
	private RootEventer eventer;
	private RootReader reader;
	private RootSyncMulticastor multicastor;
	
	public RootClient(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.multicastor = new RootSyncMulticastor(clientPool, rootBranchCount, treeBranchCount);
		this.eventer = new RootEventer(this.multicastor, pool);
		this.reader = new RootReader(this.multicastor, waitTime, pool);
	}
	
	public void close() throws InterruptedException, IOException
	{
		this.multicastor.dispose();
		this.eventer.dispose();
		this.reader.dispose();
	}

//	public RendezvousPoint getRP()
	public RendezvousPoint getRP()
	{
		return this.reader.getRP();
	}

	public void broadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}
	
	public void asyncBroadcastNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}

	/*
	 * Anycast notifications. 09/03/2018, Bing Li
	 */
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public void asyncAnycastNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}

	/*
	 * Unicast notifications. 09/03/2018, Bing Li
	 */
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncRandomNotify(notification);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public void asyncUnicastNotify(MulticastNotification notification)
	{
		this.eventer.asyncRandomNotify(notification);
	}

	/*
	 * Broadcast requests. 09/03/2018, Bing Li
	 */

	public List<MulticastResponse> broadcastRead(RPMulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncRead(request);
	}
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> broadcastRead(RPMulticastRequest request, Class<T> c) throws DistributedNodeFailedException, IOException
	{
		return Tools.filter(this.reader.syncRead(request), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncBroadcastRead(RPMulticastRequest request)
	{
		return this.reader.asyncRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> aysncBroadcastRead(RPMulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRead(request), c);
	}

	/*
	 * Anycast requests.  09/03/2018, Bing Li
	 * 
	 * n: the least count of responses
	 * 
	 */
	public List<MulticastResponse> anycastRead(RPMulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncRead(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> anycastRead(RPMulticastRequest request, int n, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncRead(request, n), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncAnycastRead(RPMulticastRequest request, int n)
	{
		return this.reader.asyncRead(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncAnycastRead(RPMulticastRequest request, int n, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRead(request, n), c);
	}

	/*
	 * Unicast requests. 09/03/2018, Bing Li
	 */
	public List<MulticastResponse> unicastRead(RPMulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastRead(RPMulticastRequest request, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncRandomRead(request), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncUnicastRead(RPMulticastRequest request)
	{
		return this.reader.asyncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncUnicastRead(RPMulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRandomRead(request), c);
	}
}
