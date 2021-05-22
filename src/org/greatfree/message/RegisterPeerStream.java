package org.greatfree.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

/*
 * The class encloses the output stream to send the response of RegisterPeerResponse to the peer. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerStream extends MessageStream<RegisterPeerRequest>
{

	public RegisterPeerStream(ObjectOutputStream out, Lock lock, RegisterPeerRequest message)
	{
		super(out, lock, message);
	}

}
