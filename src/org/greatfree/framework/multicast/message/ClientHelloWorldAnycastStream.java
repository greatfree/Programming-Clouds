package org.greatfree.framework.multicast.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldAnycastStream extends MessageStream<ClientHelloWorldAnycastRequest>
{

	public ClientHelloWorldAnycastStream(ObjectOutputStream out, Lock lock, ClientHelloWorldAnycastRequest message)
	{
		super(out, lock, message);
	}

}
