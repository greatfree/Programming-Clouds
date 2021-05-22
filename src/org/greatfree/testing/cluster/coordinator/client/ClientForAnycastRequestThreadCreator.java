package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.testing.message.ClientForAnycastRequest;
import org.greatfree.testing.message.ClientForAnycastResponse;
import org.greatfree.testing.message.ClientForAnycastStream;

/*
 * This is a class that creates the instance of ClientForUnicastRequestThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForAnycastRequestThreadCreator implements RequestQueueCreator<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread>
{

	@Override
	public ClientForAnycastRequestThread createInstance(int taskSize)
	{
		return new ClientForAnycastRequestThread(taskSize);
	}

}
