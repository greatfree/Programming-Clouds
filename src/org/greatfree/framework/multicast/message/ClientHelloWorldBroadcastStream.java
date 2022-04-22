package org.greatfree.framework.multicast.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldBroadcastStream extends MessageStream<ClientHelloWorldBroadcastRequest>
{

	public ClientHelloWorldBroadcastStream(ObjectOutputStream out, Lock lock, ClientHelloWorldBroadcastRequest message)
	{
		super(out, lock, message);
	}

}
