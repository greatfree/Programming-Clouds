package org.greatfree.dsf.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByIndexStream;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexRequestThreadCreator implements RequestThreadCreatable<PostfetchMyPointingByIndexRequest, PostfetchMyPointingByIndexStream, PostfetchMyPointingByIndexResponse, PostfetchMyPointingByIndexRequestThread>
{

	@Override
	public PostfetchMyPointingByIndexRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyPointingByIndexRequestThread(taskSize);
	}

}
