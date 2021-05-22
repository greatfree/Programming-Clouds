package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.testing.message.ClientForUnicastRequest;
import org.greatfree.testing.message.ClientForUnicastResponse;
import org.greatfree.testing.message.ClientForUnicastStream;

/*
 * This is a class that creates the instance of ClientForUnicastRequestThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForUnicastRequestThreadCreator implements RequestQueueCreator<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread>

{

	@Override
	public ClientForUnicastRequestThread createInstance(int taskSize)
	{
		return new ClientForUnicastRequestThread(taskSize);
	}

}
