package org.greatfree.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.MulticastRequest;

// Created: 08/26/2018, Bing Li
final class ChildReader
{
	private ChildSyncMulticastor multicastor;
	// The actor to perform multicasting asynchronously. 09/10/2018, Bing Li
	private ChildAsyncMulticastReader asynReader;

	public ChildReader(ChildSyncMulticastor eventer, ThreadPool pool)
	{
		this.multicastor = eventer;
		this.asynReader = new ChildAsyncMulticastReader(eventer, pool);
	}
	
	public void dispose() throws IOException, InterruptedException
	{
		this.asynReader.dispose();
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.asynReader.read(request);
	}
	
	/*
	 * Disseminate the instance of Message. The message here is the one which is just received. It must be forwarded by the local client. 11/11/2014, Bing Li
	 */
	public void syncRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		this.multicastor.read(request);
	}
}
