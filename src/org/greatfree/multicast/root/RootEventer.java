package org.greatfree.multicast.root;

import java.io.IOException;
import java.util.Set;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;

// Created: 08/26/2018, Bing Li
class RootEventer
{
//	private final static Logger log = Logger.getLogger("org.greatfree.multicast.root");

	// The multicastor to send data to children originally. 09/17/2018, Bing Li
	private RootSyncMulticastor multicastor;
	// The instance of the asynchronous based on ActorPool. 09/14/2018, Bing Li
	private RootAsyncMulticastEventer asyncEventer;

	public RootEventer(RootSyncMulticastor multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncEventer = new RootAsyncMulticastEventer(this.multicastor, pool);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncEventer.dispose();
	}
	
	public String getRandomClientKey()
	{
		return this.multicastor.getRandomClientKey();
	}
	
	/*
	 * The method is invoked when intercasting is performed. Since the remote application-level client has the application-partner ID only. The ID must have a corresponding cluster-child/client. The method return the child/client ID in the cluster. 03/12/2019, Bing Li 
	 */
	public String getNearestChildKey(String key)
	{
		return this.multicastor.getNearestClientKey(key);
	}
	
	/*
	 * Multicast data in an asynchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastNotification notification)
	{
		this.asyncEventer.asyncNotify(notification);
	}
	
	/*
	 * Multicast data in a synchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification);
	}
	
	/*
	 * Broadcast data in an synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastNotification notification, Set<String> childrenKeys)
	{
		this.asyncEventer.asyncNotify(notification, childrenKeys);
	}

	/*
	 * Broadcast data in a synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification, childrenKeys);
	}
	
	/*
	 * Broadcast data in a synchronous way within a group of randomly selected n children. 09/11/2020, Bing Li
	 */
	public void syncNotifyWithinNChildren(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Broadcast data in an asynchronous way within a group of randomly selected n children. 09/11/2020, Bing Li
	 */
	public void asyncNotifyWithinNChildren(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException
	{
		this.asyncEventer.asyncNotifyWithinNChildren(notification, childrenSize);
	}
	
	/*
	 * Unicast data in an asynchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastNotification notification, String childKey)
	{
		this.asyncEventer.asyncNotify(notification, childKey);
	}

	/*
	 * Unicast data in a synchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastNotification notification, String childKey) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification, childKey);
	}
	
	/*
	 * Unicast data in an asynchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void asyncNearestNotify(String key, MulticastNotification notification)
	{
		this.asyncEventer.asyncNearestNotify(key, notification);
	}

	/*
	 * Unicast data in a synchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void syncNearestNotify(String key, MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.nearestNotify(key, notification);
	}

	/*
	 * Unicast data in an asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void asyncRandomNotify(MulticastNotification notification)
	{
		this.asyncEventer.asyncRandomNotify(notification);
	}

	/*
	 * Unicast data in a asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void syncRandomNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
//		StressNotification sn = (StressNotification)notification;
//		log.info(sn.toString());
		this.multicastor.randomNotify(notification);
	}
}
