package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingStream;

// Created: 07/13/2018, Bing Li
public class PostfetchMyPointingRequestThreadCreator  implements RequestQueueCreator<PostfetchMyPointingByKeyRequest, PostfetchMyPointingStream, PostfetchMyPointingByKeyResponse, PostfetchMyPointingRequestThread>
{

	@Override
	public PostfetchMyPointingRequestThread createInstance(int taskSize)
	{
		return new PostfetchMyPointingRequestThread(taskSize);
	}

}
