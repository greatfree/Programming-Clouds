package org.greatfree.dsf.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByIndexThreadCreator implements RequestThreadCreatable<PostfetchMyCachePointingByIndexRequest, PostfetchMyCachePointingByIndexStream, PostfetchMyCachePointingByIndexResponse, PostfetchMyCachePointingByIndexThread>
{

	@Override
	public PostfetchMyCachePointingByIndexThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyCachePointingByIndexThread(taskSize);
	}

}
