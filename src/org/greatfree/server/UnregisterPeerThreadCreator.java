package org.greatfree.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.UnregisterPeerRequest;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.UnregisterPeerStream;

/*
 * The creator generates one instance of UnregisterPeerThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class UnregisterPeerThreadCreator implements RequestThreadCreatable<UnregisterPeerRequest, UnregisterPeerStream, UnregisterPeerResponse, UnregisterPeerThread>
{

	@Override
	public UnregisterPeerThread createRequestThreadInstance(int taskSize)
	{
		return new UnregisterPeerThread(taskSize);
	}

}
