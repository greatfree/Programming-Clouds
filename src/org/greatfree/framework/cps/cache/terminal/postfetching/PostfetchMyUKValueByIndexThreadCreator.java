package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexStream;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValueByIndexThreadCreator implements RequestQueueCreator<PostfetchMyUKValueByIndexRequest, PostfetchMyUKValueByIndexStream, PostfetchMyUKValueByIndexResponse, PostfetchMyUKValueByIndexThread>
{

	@Override
	public PostfetchMyUKValueByIndexThread createInstance(int taskSize)
	{
		return new PostfetchMyUKValueByIndexThread(taskSize);
	}

}
