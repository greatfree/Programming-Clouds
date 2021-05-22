package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyThreadCreator implements RequestQueueCreator<PostfetchMyCachePointingByKeyRequest, PostfetchMyCachePointingByKeyStream, PostfetchMyCachePointingByKeyResponse, PostfetchMyCachePointingByKeyThread>
{

	@Override
	public PostfetchMyCachePointingByKeyThread createInstance(int taskSize)
	{
		return new PostfetchMyCachePointingByKeyThread(taskSize);
	}

}
