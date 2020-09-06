package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.dsf.multicast.message.ClientHelloWorldUnicastStream;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastRequestThreadCreator implements RequestThreadCreatable<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread>
{

	@Override
	public ClientHelloWorldUnicastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastRequestThread(taskSize);
	}

}
