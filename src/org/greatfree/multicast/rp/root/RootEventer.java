package org.greatfree.multicast.rp.root;

import java.io.IOException;
import java.util.Set;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;

// Created: 10/15/2018, Bing Li
class RootEventer
{
	// The multicastor to send data to children originally. 09/17/2018, Bing Li
	private RootSyncMulticastor multicastor;
	
	private RootAsyncMulticastEventer asyncEventer;
	
	public RootEventer(RootSyncMulticastor multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncEventer = new RootAsyncMulticastEventer(multicastor, pool);
	}
	
	public void dispose() throws InterruptedException
	{
		this.asyncEventer.dispose();
	}

	/*
	 * Multicast data in an asynchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastMessage notification)
	{
		this.asyncEventer.asyncNotify(notification);
	}
	
	/*
	 * Multicast data in a synchronous way. It could be a broadcast or an anycast. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification);
	}
	
	/*
	 * Broadcast data in an synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastMessage notification, Set<String> childrenKeys)
	{
		this.asyncEventer.asyncNotify(notification, childrenKeys);
	}

	/*
	 * Broadcast data in a synchronous way within a group of specified children. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastMessage notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification, childrenKeys);
	}
	
	/*
	 * Unicast data in an asynchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void asyncNotify(MulticastMessage notification, String childKey)
	{
		this.asyncEventer.asyncNotify(notification, childKey);
	}

	/*
	 * Unicast data in a synchronous way to a specified child. 09/15/2018, Bing Li
	 */
	public void syncNotify(MulticastMessage notification, String childKey) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification, childKey);
	}
	
	/*
	 * Unicast data in an asynchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void asyncNearestNotify(String key, MulticastMessage notification)
	{
		this.asyncEventer.asyncNearestNotify(key, notification);
	}

	/*
	 * Unicast data in a synchronous way to a nearest child in terms of the similarity to the specified key. 09/15/2018, Bing Li
	 */
	public void syncNearestNotify(String key, MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.nearestNotify(key, notification);
	}

	/*
	 * Unicast data in an asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void asyncRandomNotify(MulticastMessage notification)
	{
		this.asyncEventer.asyncRandomNotify(notification);
	}

	/*
	 * Unicast data in a asynchronous way to a child that is selected randomly. 09/15/2018, Bing Li
	 */
	public void syncRandomNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.randomNotify(notification);
	}
}
