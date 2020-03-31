package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.testing.message.SignUpRequest;
import org.greatfree.testing.message.SignUpResponse;
import org.greatfree.testing.message.SignUpStream;

/*
 * A creator to initialize instances of SignUpThread. It is used in the instance of RequestDispatcher. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
class SignUpThreadCreator implements RequestThreadCreatable<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread>
{
	@Override
	public SignUpThread createRequestThreadInstance(int taskSize)
	{
		return new SignUpThread(taskSize);
	}
}
