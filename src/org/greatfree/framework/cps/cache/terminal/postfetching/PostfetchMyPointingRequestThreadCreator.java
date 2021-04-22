package org.greatfree.framework.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingStream;

// Created: 07/13/2018, Bing Li
public class PostfetchMyPointingRequestThreadCreator  implements RequestThreadCreatable<PostfetchMyPointingByKeyRequest, PostfetchMyPointingStream, PostfetchMyPointingByKeyResponse, PostfetchMyPointingRequestThread>
{

	@Override
	public PostfetchMyPointingRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMyPointingRequestThread(taskSize);
	}

}
