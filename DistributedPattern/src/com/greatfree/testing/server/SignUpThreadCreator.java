package com.greatfree.testing.server;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.SignUpRequest;
import com.greatfree.testing.message.SignUpResponse;
import com.greatfree.testing.message.SignUpStream;

/*
 * A creator to initialize instances of SignUpThread. It is used in the instance of RequestDispatcher. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class SignUpThreadCreator implements RequestThreadCreatable<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread>
{
	@Override
	public SignUpThread createRequestThreadInstance(int taskSize)
	{
		return new SignUpThread(taskSize);
	}
}
