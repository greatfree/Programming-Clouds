package edu.greatfree.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.cs.multinode.message.PollNewChatsRequest;
import edu.greatfree.cs.multinode.message.PollNewChatsResponse;
import edu.greatfree.cs.multinode.message.PollNewChatsStream;

// Created: 04/24/2017, Bing Li
class PollNewChatsThreadCreator implements RequestThreadCreatable<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse, PollNewChatsThread>
{

	@Override
	public PollNewChatsThread createRequestThreadInstance(int taskSize)
	{
		return new PollNewChatsThread(taskSize);
	}

}
