package org.greatfree.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/14/2018, Bing Li
class ChildEventer
{
	private ChildSyncMulticastor multicastor;

	private ChildAsyncMulticastEventer asyncEventer;
	
	public ChildEventer(ChildSyncMulticastor multicastor, ThreadPool pool)
	{
		this.multicastor = multicastor;
		this.asyncEventer = new ChildAsyncMulticastEventer(multicastor, pool);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asyncEventer.dispose();
	}
	
	public void notify(String ip, int port, MulticastResponse response) throws IOException, InterruptedException
	{
		this.multicastor.notify(ip, port, response);
	}

	/*
	 * Disseminate the instance of Message asynchronously. 11/11/2014, Bing Li
	 */
	public void asyncNotify(MulticastNotification notification)
	{
		this.asyncEventer.notify(notification);
	}
	
	/*
	 * Disseminate the instance of Message synchronously. 11/11/2014, Bing Li
	 */
	public void syncNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException, InterruptedException
	{
		this.multicastor.notify(notification);
	}
}
