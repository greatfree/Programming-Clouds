package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.streaming.news.message.IsVideoExistedRequest;
import ca.streaming.news.message.IsVideoExistedResponse;
import ca.streaming.news.message.IsVideoExistedStream;

// Created: 04/05/2020, Bing Li
class IsVideoExistedRequestThreadCreator implements RequestThreadCreatable<IsVideoExistedRequest, IsVideoExistedStream, IsVideoExistedResponse, IsVideoExistedRequestThread>
{

	@Override
	public IsVideoExistedRequestThread createRequestThreadInstance(int taskSize)
	{
		return new IsVideoExistedRequestThread(taskSize);
	}

}
