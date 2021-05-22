package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.ClientHelloWorldBroadcastStream;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldBroadcastRequestThreadCreator implements RequestQueueCreator<ClientHelloWorldBroadcastRequest, ClientHelloWorldBroadcastStream, ClientHelloWorldBroadcastResponse, ClientHelloWorldBroadcastRequestThread>
{

	@Override
	public ClientHelloWorldBroadcastRequestThread createInstance(int taskSize)
	{
		return new ClientHelloWorldBroadcastRequestThread(taskSize);
	}

}
