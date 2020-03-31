package edu.greatfree.p2p.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

/*
 * The class encloses the output stream to send the response of ChatRegistryResponse to the client. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatRegistryStream extends OutMessageStream<ChatRegistryRequest>
{

	public ChatRegistryStream(ObjectOutputStream out, Lock lock, ChatRegistryRequest message)
	{
		super(out, lock, message);
	}

}
