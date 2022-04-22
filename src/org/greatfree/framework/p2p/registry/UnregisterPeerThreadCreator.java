package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.UnregisterPeerRequest;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.UnregisterPeerStream;

/*
 * The creator generates one instance of UnregisterPeerThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
class UnregisterPeerThreadCreator implements RequestQueueCreator<UnregisterPeerRequest, UnregisterPeerStream, UnregisterPeerResponse, UnregisterPeerThread>
{

	@Override
	public UnregisterPeerThread createInstance(int taskSize)
	{
		return new UnregisterPeerThread(taskSize);
	}

}
