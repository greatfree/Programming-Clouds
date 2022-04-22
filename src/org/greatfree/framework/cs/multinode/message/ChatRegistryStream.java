package org.greatfree.framework.cs.multinode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class encloses the output stream to send the response of ChatRegistryResponse to the client. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryStream extends MessageStream<ChatRegistryRequest>
{

	public ChatRegistryStream(ObjectOutputStream out, Lock lock, ChatRegistryRequest message)
	{
		super(out, lock, message);
	}

}
