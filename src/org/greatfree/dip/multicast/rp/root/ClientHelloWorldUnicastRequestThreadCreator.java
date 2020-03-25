package org.greatfree.dip.multicast.rp.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.multicast.message.ClientHelloWorldUnicastRequest;
import org.greatfree.dip.multicast.message.ClientHelloWorldUnicastResponse;
import org.greatfree.dip.multicast.message.ClientHelloWorldUnicastStream;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldUnicastRequestThreadCreator implements RequestThreadCreatable<ClientHelloWorldUnicastRequest, ClientHelloWorldUnicastStream, ClientHelloWorldUnicastResponse, ClientHelloWorldUnicastRequestThread>
{

	@Override
	public ClientHelloWorldUnicastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastRequestThread(taskSize);
	}

}
