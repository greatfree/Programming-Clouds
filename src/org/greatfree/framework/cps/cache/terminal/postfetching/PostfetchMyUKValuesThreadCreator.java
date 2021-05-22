package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesStream;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValuesThreadCreator implements RequestQueueCreator<PostfetchMyUKValuesRequest, PostfetchMyUKValuesStream, PostfetchMyUKValuesResponse, PostfetchMyUKValuesThread>
{

	@Override
	public PostfetchMyUKValuesThread createInstance(int taskSize)
	{
		return new PostfetchMyUKValuesThread(taskSize);
	}

}
