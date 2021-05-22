package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataStream;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataRequestThreadCreator implements RequestQueueCreator<PostfetchMyDataRequest, PostfetchMyDataStream, PostfetchMyDataResponse, PostfetchMyDataRequestThread>
{

	@Override
	public PostfetchMyDataRequestThread createInstance(int taskSize)
	{
		return new PostfetchMyDataRequestThread(taskSize);
	}

}
