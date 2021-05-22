package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexStream;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexRequestThreadCreator implements RequestQueueCreator<PostfetchMyPointingByIndexRequest, PostfetchMyPointingByIndexStream, PostfetchMyPointingByIndexResponse, PostfetchMyPointingByIndexRequestThread>
{

	@Override
	public PostfetchMyPointingByIndexRequestThread createInstance(int taskSize)
	{
		return new PostfetchMyPointingByIndexRequestThread(taskSize);
	}

}
