package org.greatfree.testing.cluster.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.BoundBroadcastRequestThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.DNBroadcastRequest;
import org.greatfree.testing.message.DNBroadcastResponse;

/*
 * The creator is responsible for initializing an instance of BroadcastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastRequestThreadCreator implements BoundBroadcastRequestThreadCreatable<DNBroadcastRequest, DNBroadcastResponse, MulticastMessageDisposer<DNBroadcastRequest>, BroadcastRequestThread>
{

	@Override
	public BroadcastRequestThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<DNBroadcastRequest> reqBinder)
	{
		return new BroadcastRequestThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

}
