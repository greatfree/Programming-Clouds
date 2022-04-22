package org.greatfree.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class encloses the output stream to send the response of UnregisterPeerResponse to the peer. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class UnregisterPeerStream extends MessageStream<UnregisterPeerRequest>
{

	public UnregisterPeerStream(ObjectOutputStream out, Lock lock, UnregisterPeerRequest message)
	{
		super(out, lock, message);
	}

}
