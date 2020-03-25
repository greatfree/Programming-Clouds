package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastMessage;

// Created: 08/26/2018, Bing Li
class ChildEventer
{
	private ChildSyncMulticastor multicastor;
	// Declare an instance to send multicasting messages asynchronously.. 11/11/2014, Bing Li
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
	
	/*
	 * Disseminate the instance of Message asynchronously. 11/11/2014, Bing Li
	 */
	public void asyncNotify(MulticastMessage notification)
	{
		this.asyncEventer.notify(notification);
	}
	
	/*
	 * Disseminate the instance of Message synchronously. 11/11/2014, Bing Li
	 */
	public void syncNotify(MulticastMessage notification) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.notify(notification);
	}
}
