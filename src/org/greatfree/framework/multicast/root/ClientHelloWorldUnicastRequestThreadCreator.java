package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastStream;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastRequestThreadCreator implements RequestQueueCreator<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread>
{

	@Override
	public ClientHelloWorldUnicastRequestThread createInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastRequestThread(taskSize);
	}

}
