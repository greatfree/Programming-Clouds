package org.greatfree.framework.multicast.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldUnicastStream extends MessageStream<ClientHelloWorldUnicastRequest>
{

	public ClientHelloWorldUnicastStream(ObjectOutputStream out, Lock lock, ClientHelloWorldUnicastRequest message)
	{
		super(out, lock, message);
	}

}
