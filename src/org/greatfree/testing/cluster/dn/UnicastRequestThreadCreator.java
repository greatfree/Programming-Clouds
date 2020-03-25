package org.greatfree.testing.cluster.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.BoundBroadcastRequestThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.UnicastRequest;
import org.greatfree.testing.message.UnicastResponse;

/*
 * The creator is responsible for initializing an instance of UnicastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/26/2016, Bing Li
public class UnicastRequestThreadCreator implements BoundBroadcastRequestThreadCreatable<UnicastRequest, UnicastResponse, MulticastMessageDisposer<UnicastRequest>, UnicastRequestThread>
{

	@Override
	public UnicastRequestThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<UnicastRequest> reqBinder)
	{
		return new UnicastRequestThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

}
