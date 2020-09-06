package org.greatfree.dsf.multicast.rp.root;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.dsf.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.dsf.multicast.message.ClientHelloWorldBroadcastStream;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldBroadcastRequestThreadCreator implements RequestThreadCreatable<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse, ClientHelloWorldBroadcastRequestThread>
{

	@Override
	public ClientHelloWorldBroadcastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientHelloWorldBroadcastRequestThread(taskSize);
	}

}
