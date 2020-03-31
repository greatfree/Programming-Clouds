package ca.dp.mncs.circle.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.dp.mncs.circle.message.PostRequest;
import ca.dp.mncs.circle.message.PostResponse;
import ca.dp.mncs.circle.message.PostStream;

// Created: 02/26/2020, Bing Li
class PostRequestThreadCreator implements RequestThreadCreatable<PostRequest, PostStream, PostResponse, PostRequestThread>
{

	@Override
	public PostRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostRequestThread(taskSize);
	}

}
