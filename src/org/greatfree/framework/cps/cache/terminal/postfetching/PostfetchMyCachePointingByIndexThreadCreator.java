package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByIndexThreadCreator implements RequestQueueCreator<PostfetchMyCachePointingByIndexRequest, PostfetchMyCachePointingByIndexStream, PostfetchMyCachePointingByIndexResponse, PostfetchMyCachePointingByIndexThread>
{

	@Override
	public PostfetchMyCachePointingByIndexThread createInstance(int taskSize)
	{
		return new PostfetchMyCachePointingByIndexThread(taskSize);
	}

}
