package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataThreadCreator implements RequestThreadCreatable<PostfetchMuchMyStoreDataRequest, PostfetchMuchMyStoreDataStream, PostfetchMuchMyStoreDataResponse, PostfetchMuchMyStoreDataThread>
{

	@Override
	public PostfetchMuchMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMuchMyStoreDataThread(taskSize);
	}

}
