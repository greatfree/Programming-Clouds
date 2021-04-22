package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsStream;

// Created: 07/13/2018, Bing Li
public class PostfetchMyPointingsRequestThreadCreator implements RequestThreadCreatable<PostfetchMyPointingsRequest, PostfetchMyPointingsStream, PostfetchMyPointingsResponse, PostfetchMyPointingsRequestThread>
{

	@Override
	public PostfetchMyPointingsRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyPointingsRequestThread(taskSize);
	}

}
