package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cs.multinode.message.PollNewChatsRequest;
import org.greatfree.framework.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewChatsStream;

// Created: 04/24/2017, Bing Li
class PollNewChatsThreadCreator implements RequestQueueCreator<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse, PollNewChatsThread>
{

	@Override
	public PollNewChatsThread createInstance(int taskSize)
	{
		return new PollNewChatsThread(taskSize);
	}

}
