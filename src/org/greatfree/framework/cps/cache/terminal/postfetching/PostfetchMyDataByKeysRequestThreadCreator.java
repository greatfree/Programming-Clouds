package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysStream;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysRequestThreadCreator implements RequestQueueCreator<PostfetchMyDataByKeysRequest, PostfetchMyDataByKeysStream, PostfetchMyDataByKeysResponse, PostfetchMyDataByKeysRequestThread>
{

	@Override
	public PostfetchMyDataByKeysRequestThread createInstance(int taskSize)
	{
		return new PostfetchMyDataByKeysRequestThread(taskSize);
	}

}
