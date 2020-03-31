package ca.dp.mncs.circle.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.dp.mncs.circle.message.PollLikeRequest;
import ca.dp.mncs.circle.message.PollLikeResponse;
import ca.dp.mncs.circle.message.PollLikeStream;

// Created: 02/26/2020, Bing Li
class PollLikeRequestThreadCreator implements RequestThreadCreatable<PollLikeRequest, PollLikeStream, PollLikeResponse, PollLikeRequestThread>
{

	@Override
	public PollLikeRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PollLikeRequestThread(taskSize);
	}

}
