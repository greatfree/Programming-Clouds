package org.greatfree.dip.cps.cache.terminal.postfetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMinMyPointingRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMinMyPointingStream;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingRequestThreadCreator implements RequestThreadCreatable<PostfetchMinMyPointingRequest, PostfetchMinMyPointingStream, PostfetchMinMyPointingResponse, PostfetchMinMyPointingRequestThread>
{

	@Override
	public PostfetchMinMyPointingRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PostfetchMinMyPointingRequestThread(taskSize);
	}

}
