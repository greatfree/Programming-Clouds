package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysThreadCreator implements RequestQueueCreator<PostfetchMyStoreDataKeysRequest, PostfetchMyStoreDataKeysStream, PostfetchMyStoreDataKeysResponse, PostfetchMyStoreDataKeysThread>
{

	@Override
	public PostfetchMyStoreDataKeysThread createInstance(int taskSize)
	{
		return new PostfetchMyStoreDataKeysThread(taskSize);
	}

}
