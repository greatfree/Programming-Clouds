package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldUnicastStream;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastRequestThreadCreator implements RequestThreadCreatable<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread>
{

	@Override
	public ClientHelloWorldUnicastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastRequestThread(taskSize);
	}

}
