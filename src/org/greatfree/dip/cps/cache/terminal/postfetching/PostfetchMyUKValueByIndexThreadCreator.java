package org.greatfree.dip.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexStream;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValueByIndexThreadCreator implements RequestThreadCreatable<PostfetchMyUKValueByIndexRequest, PostfetchMyUKValueByIndexStream, PostfetchMyUKValueByIndexResponse, PostfetchMyUKValueByIndexThread>
{

	@Override
	public PostfetchMyUKValueByIndexThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyUKValueByIndexThread(taskSize);
	}

}
