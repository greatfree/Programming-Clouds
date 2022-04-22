package org.greatfree.testing.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class is derived from OutMessageStream. It contains the received request, its associated output stream and the lock that keeps the responding operations atomic. 11/29/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForAnycastStream extends MessageStream<ClientForAnycastRequest>
{

	public ClientForAnycastStream(ObjectOutputStream out, Lock lock, ClientForAnycastRequest message)
	{
		super(out, lock, message);
	}

}
