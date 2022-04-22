package org.greatfree.testing.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class is derived from OutMessageStream. It contains the received sign up request, its associated output stream and the lock that keeps the responding operations atomic. 09/22/2014, Bing Li
 */

// Created: 09/22/2014, Bing Li
public class SignUpStream extends MessageStream<SignUpRequest>
{
	// Initialize the instance of the request stream. 09/22/2014, Bing Li
	public SignUpStream(ObjectOutputStream out, Lock lock, SignUpRequest request)
	{
		super(out, lock, request);
	}
}
