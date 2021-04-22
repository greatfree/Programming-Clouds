package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataStream;

// Created: 09/25/2018, Bing Li
public class PostfetchMyStoreDataThreadCreator implements RequestThreadCreatable<PostfetchMyStoreDataRequest, PostfetchMyStoreDataStream, PostfetchMyStoreDataResponse, PostfetchMyStoreDataThread>
{

	@Override
	public PostfetchMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyStoreDataThread(taskSize);
	}

}
