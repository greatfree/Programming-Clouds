package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsThreadCreator implements RequestQueueCreator<PostfetchMyCachePointingsRequest, PostfetchMyCachePointingsStream, PostfetchMyCachePointingsResponse, PostfetchMyCachePointingsThread>
{

	@Override
	public PostfetchMyCachePointingsThread createInstance(int taskSize)
	{
		return new PostfetchMyCachePointingsThread(taskSize);
	}

}
