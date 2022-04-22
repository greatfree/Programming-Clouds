package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.RegisterPeerStream;

/*
 * The creator generates one instance of RegisterPeerThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
class RegisterPeerThreadCreator implements RequestQueueCreator<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse, RegisterPeerThread>
{

	@Override
	public RegisterPeerThread createInstance(int taskSize)
	{
		return new RegisterPeerThread(taskSize);
	}

}
