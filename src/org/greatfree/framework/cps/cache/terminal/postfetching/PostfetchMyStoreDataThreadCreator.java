package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataStream;

// Created: 09/25/2018, Bing Li
public class PostfetchMyStoreDataThreadCreator implements RequestQueueCreator<PostfetchMyStoreDataRequest, PostfetchMyStoreDataStream, PostfetchMyStoreDataResponse, PostfetchMyStoreDataThread>
{

	@Override
	public PostfetchMyStoreDataThread createInstance(int taskSize)
	{
		return new PostfetchMyStoreDataThread(taskSize);
	}

}
