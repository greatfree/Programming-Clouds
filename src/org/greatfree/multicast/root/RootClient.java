package org.greatfree.multicast.root;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.util.Tools;

// Created: 09/03/2018, Bing Li
public class RootClient
{
	private RootEventer eventer;
	private RootReader reader;
	private RootSyncMulticastor multicastor;

	/*
	 * The constructor. 09/03/2018, Bing Li
	 * 
	 *  Initialize the root client.
	 * 
	 * clientPool: the TCP client management mechanism
	 * 
	 * rootBranchCount: the immediate connected children count of the root
	 * 
	 * treeBranchCount: the branch count of multicasting tree except rootBranchCount
	 * 
	 */
	public RootClient(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, long waitTime, ThreadPool pool)
	{
		this.multicastor = new RootSyncMulticastor(clientPool, rootBranchCount, treeBranchCount);
		this.eventer = new RootEventer(this.multicastor, pool);
		this.reader = new RootReader(this.multicastor, waitTime, pool);
//		this.multicastor = new SourceMulticastor(clientPool, rootBranchCount, treeBranchCount, this.reader.getRP());
	}

	/*
	 * Close the root client. 09/03/2018, Bing Li
	 */
	public void close() throws IOException, InterruptedException
	{
		this.multicastor.dispose();
		this.eventer.dispose();
		this.reader.dispose();
	}
	
	public void clearChildren()
	{
		this.multicastor.clearChildren();
	}

	public void addChild(String ip, int port)
	{
		this.multicastor.addIP(ip, port);
	}

	/*
	 * Expose the rendezvous point of the root. 09/03/2018, Bing Li
	 */
	public RootRendezvousPoint getRP()
	{
		return this.reader.getRP();
	}
	
	public String getRandomChildKey()
	{
		return this.eventer.getRandomClientKey();
	}

	/*
	 * The method is invoked when intercasting is performed. Since the remote application-level client has the application-partner ID only. The ID must have a corresponding cluster-child/client. The method return the child/client ID in the cluster. 03/12/2019, Bing Li 
	 */
	public String getNearestChildKey(String key)
	{
		return this.eventer.getNearestChildKey(key);
	}
	
	/*
	 * Broadcast notifications. 09/03/2018, Bing Li
	 */
	public void broadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}

	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public void asyncBroadcastNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}
	
	/*
	 * Broadcast notifications. 02/23/2019, Bing Li
	 */
	public void broadcastNotify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification, childrenKeys);
	}

	/*
	 * The method has not been tested although it should be correct. 02/23/2019, Bing Li
	 */
	public void asyncBroadcastNotify(MulticastNotification notification, Set<String> childrenKeys)
	{
		this.eventer.asyncNotify(notification, childrenKeys);
	}
	
	/*
	 * Broadcasting a notification synchronously within N randomly selected children. 09/11/2020, Bing Li
	 */
	public void broadcastNotify(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Broadcasting a notification asynchronously within N randomly selected children. 09/11/2020, Bing Li
	 */
	public void asyncBroadcastNotify(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.eventer.asyncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Anycast notifications. 09/03/2018, Bing Li
	 */
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public void asyncAnycastNotify(MulticastNotification notification)
	{
		this.eventer.asyncNotify(notification);
	}

	/*
	 * Anycast notifications. 09/03/2018, Bing Li
	 */
	public void anycastNotify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNotify(notification, childrenKeys);
	}
	
	/*
	 * The method has not been tested although it should be correct. 02/23/2019, Bing Li
	 */
	public void asyncAnycastNotify(MulticastNotification notification, Set<String> childrenKeys)
	{
		this.eventer.asyncNotify(notification, childrenKeys);
	}
	
	/*
	 * Unicast notifications. 09/03/2018, Bing Li
	 */
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncRandomNotify(notification);
	}
	
	/*
	 * Unicast notifications to the nearest child. 09/03/2018, Bing Li
	 */
	public void unicastNearestNotify(String clientKey, MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.eventer.syncNearestNotify(clientKey, notification);
	}
	
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public void asyncUnicastNotify(MulticastNotification notification)
	{
		this.eventer.asyncRandomNotify(notification);
	}
	
	/*
	 * Unicast notifications to the nearest child asynchronously. 09/03/2018, Bing Li
	 */
	public void asyncUnicastNearestNotify(String clientKey, MulticastNotification notification)
	{
		this.eventer.asyncNearestNotify(clientKey, notification);
	}
	
	/*
	 * Broadcast requests. 09/03/2018, Bing Li
	 */
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncRead(request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> broadcastRead(MulticastRequest request, Class<T> c) throws DistributedNodeFailedException, IOException
	{
		return Tools.filter(this.reader.syncRead(request), c);
	}
	
	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request)
	{
		return this.reader.asyncRead(request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> aysncBroadcastRead(MulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRead(request), c);
	}
	
	public List<MulticastResponse> unicastRead(MulticastRequest request, String childKey) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncRead(childKey, request);
	}
	
	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastRead(MulticastRequest request, String childKey, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncRead(childKey, request), c);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncRead(childrenKeys, request);
	}

	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request, Set<String> childrenKeys)
	{
		return this.reader.asyncRead(childrenKeys, request);
	}

	/*
	 * The method waits for a single response from one particular partition. 09/08/2020, Bing Li
	 */
	public MulticastResponse broadcastReadByPartition(MulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncReadUponPartition(childrenKeys, request);
	}
	
	/*
	 * The method waits for a single response from one particular partition. 09/08/2020, Bing Li
	 */
	public MulticastResponse asyncBroadcastReadByPartition(MulticastRequest request, Set<String> childrenKeys)
	{
		return this.reader.asyncReadUponPartition(childrenKeys, request);
	}
	
	/*
	 * Broadcasting a request synchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> broadcastReadWithinNChildren(MulticastRequest request, int n) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncReadWithinNChildren(n, request);
	}
	
	/*
	 * Broadcasting a request asynchronously within randomly selected N children. 09/11/2020, Bing Li
	 */
	public List<MulticastResponse> asyncBroadcastReadWithinNChildren(MulticastRequest request, int n)
	{
		return this.reader.asyncReadWithinNChildren(n, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> broadcastRead(MulticastRequest request, Set<String> childrenKeys, Class<T> c) throws DistributedNodeFailedException, IOException
	{
		return Tools.filter(this.reader.syncRead(childrenKeys, request), c);
	}

	/*
	 * Anycast requests.  09/03/2018, Bing Li
	 * 
	 * n: the least count of responses
	 * 
	 */
	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncReadWithNResponses(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> anycastRead(MulticastRequest request, int n, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncReadWithNResponses(request, n), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09//15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncAnycastRead(MulticastRequest request, int n)
	{
		return this.reader.asyncReadWithNResponses(request, n);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncAnycastRead(MulticastRequest request, int n, Class<T> c)
	{
		return Tools.filter(this.reader.asyncReadWithNResponses(request, n), c);
	}

	public List<MulticastResponse> anycastRead(MulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException
	{
		return this.reader.syncRead(childrenKeys, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> anycastRead(MulticastRequest request, Set<String> childrenKeys, Class<T> c) throws DistributedNodeFailedException, IOException
	{
		return Tools.filter(this.reader.syncRead(childrenKeys, request), c);
	}
	
	/*
	 * Unicast requests. 09/03/2018, Bing Li
	 */
	public List<MulticastResponse> unicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastRead(MulticastRequest request, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncRandomRead(request), c);
	}
	
	/*
	 * Unicast requests to the nearest child. 09/03/2018, Bing Li
	 */
	public List<MulticastResponse> unicastNearestRead(String clientKey, MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.reader.syncNearestRead(clientKey, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> unicastNearestRead(String clientKey, MulticastRequest request, Class<T> c) throws IOException, DistributedNodeFailedException
	{
		return Tools.filter(this.reader.syncNearestRead(clientKey, request), c);
	}

	/*
	 * The method has not been tested although it should be correct. 09/15/2018, Bing Li
	 */
	public List<MulticastResponse> asyncUnicastRead(MulticastRequest request)
	{
		return this.reader.asyncRandomRead(request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncUnicastRead(MulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncRandomRead(request), c);
	}

	/*
	 * Unicast requests to the nearest child asynchronously. 09/03/2018, Bing Li
	 */
	public List<MulticastResponse> asyncUnicastNearestRead(String clientKey, MulticastRequest request)
	{
		return this.reader.asyncNearestRead(clientKey, request);
	}

	/*
	 * The method is just added to simplify the conversion between the list of parent objects and the list of child objects. 03/13/2020, Bing Li 
	 */
	public <T> List<T> asyncUnicastNearestRead(String clientKey, MulticastRequest request, Class<T> c)
	{
		return Tools.filter(this.reader.asyncNearestRead(clientKey, request), c);
	}
}
