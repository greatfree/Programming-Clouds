package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingStream;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingRequestThreadCreator implements RequestQueueCreator<PostfetchMinMyPointingRequest, PostfetchMinMyPointingStream, PostfetchMinMyPointingResponse, PostfetchMinMyPointingRequestThread>
{

	@Override
	public PostfetchMinMyPointingRequestThread createInstance(int taskSize)
	{
		return new PostfetchMinMyPointingRequestThread(taskSize);
	}

}
