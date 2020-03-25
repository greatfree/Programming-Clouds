package org.greatfree.dip.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cs.multinode.message.PollNewChatsRequest;
import org.greatfree.dip.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.dip.cs.multinode.message.PollNewChatsStream;

// Created: 04/24/2017, Bing Li
class PollNewChatsThreadCreator implements RequestThreadCreatable<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse, PollNewChatsThread>
{

	@Override
	public PollNewChatsThread createRequestThreadInstance(int taskSize)
	{
		return new PollNewChatsThread(taskSize);
	}

}
