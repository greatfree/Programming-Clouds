package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsStream extends OutMessageStream<PostfetchMyCachePointingsRequest>
{

	public PostfetchMyCachePointingsStream(ObjectOutputStream out, Lock lock, PostfetchMyCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
