package org.greatfree.dip.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyCachePointingsStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsThreadCreator implements RequestThreadCreatable<PostfetchMyCachePointingsRequest, PostfetchMyCachePointingsStream, PostfetchMyCachePointingsResponse, PostfetchMyCachePointingsThread>
{

	@Override
	public PostfetchMyCachePointingsThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyCachePointingsThread(taskSize);
	}

}
