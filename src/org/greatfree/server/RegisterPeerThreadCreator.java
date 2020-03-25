package org.greatfree.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.RegisterPeerStream;

/*
 * The creator generates one instance of RegisterPeerThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class RegisterPeerThreadCreator implements RequestThreadCreatable<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse, RegisterPeerThread>
{

	@Override
	public RegisterPeerThread createRequestThreadInstance(int taskSize)
	{
		return new RegisterPeerThread(taskSize);
	}

}
