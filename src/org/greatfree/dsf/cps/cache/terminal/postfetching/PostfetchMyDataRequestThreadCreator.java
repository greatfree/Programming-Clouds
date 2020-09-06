package org.greatfree.dsf.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataStream;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataRequestThreadCreator implements RequestThreadCreatable<PostfetchMyDataRequest, PostfetchMyDataStream, PostfetchMyDataResponse, PostfetchMyDataRequestThread>
{

	@Override
	public PostfetchMyDataRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyDataRequestThread(taskSize);
	}

}
