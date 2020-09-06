package org.greatfree.dsf.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyThreadCreator implements RequestThreadCreatable<PostfetchMyCachePointingByKeyRequest, PostfetchMyCachePointingByKeyStream, PostfetchMyCachePointingByKeyResponse, PostfetchMyCachePointingByKeyThread>
{

	@Override
	public PostfetchMyCachePointingByKeyThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyCachePointingByKeyThread(taskSize);
	}

}
