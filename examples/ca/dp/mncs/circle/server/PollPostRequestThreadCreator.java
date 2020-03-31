package ca.dp.mncs.circle.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.dp.mncs.circle.message.PollPostRequest;
import ca.dp.mncs.circle.message.PollPostResponse;
import ca.dp.mncs.circle.message.PollPostStream;

// Created: 02/25/2020, Bing Li
class PollPostRequestThreadCreator implements RequestThreadCreatable<PollPostRequest, PollPostStream, PollPostResponse, PollPostRequestThread>
{

	@Override
	public PollPostRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PollPostRequestThread(taskSize);
	}

}
