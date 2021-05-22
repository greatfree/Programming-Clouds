package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldAnycastStream;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldAnycastRequestThreadCreator implements RequestQueueCreator<ClientHelloWorldAnycastRequest, ClientHelloWorldAnycastStream, ClientHelloWorldAnycastResponse, ClientHelloWorldAnycastRequestThread>
{

	@Override
	public ClientHelloWorldAnycastRequestThread createInstance(int taskSize)
	{
		return new ClientHelloWorldAnycastRequestThread(taskSize);
	}

}
