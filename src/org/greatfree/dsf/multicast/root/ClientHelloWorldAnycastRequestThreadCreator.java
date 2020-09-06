package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.multicast.message.ClientHelloWorldAnycastRequest;
import org.greatfree.dsf.multicast.message.ClientHelloWorldAnycastResponse;
import org.greatfree.dsf.multicast.message.ClientHelloWorldAnycastStream;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldAnycastRequestThreadCreator implements RequestThreadCreatable<ClientHelloWorldAnycastRequest, ClientHelloWorldAnycastStream, ClientHelloWorldAnycastResponse, ClientHelloWorldAnycastRequestThread>
{

	@Override
	public ClientHelloWorldAnycastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientHelloWorldAnycastRequestThread(taskSize);
	}

}
