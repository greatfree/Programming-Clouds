package org.greatfree.dsf.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysStream;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysRequestThreadCreator implements RequestThreadCreatable<PostfetchMyDataByKeysRequest, PostfetchMyDataByKeysStream, PostfetchMyDataByKeysResponse, PostfetchMyDataByKeysRequestThread>
{

	@Override
	public PostfetchMyDataByKeysRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyDataByKeysRequestThread(taskSize);
	}

}
