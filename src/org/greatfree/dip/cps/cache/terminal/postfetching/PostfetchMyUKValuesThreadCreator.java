package org.greatfree.dip.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValuesRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValuesStream;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValuesThreadCreator implements RequestThreadCreatable<PostfetchMyUKValuesRequest, PostfetchMyUKValuesStream, PostfetchMyUKValuesResponse, PostfetchMyUKValuesThread>
{

	@Override
	public PostfetchMyUKValuesThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyUKValuesThread(taskSize);
	}

}
