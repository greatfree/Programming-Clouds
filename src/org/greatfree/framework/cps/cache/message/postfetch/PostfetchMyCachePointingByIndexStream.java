package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/24/2018, Bing Li
public class PostfetchMyCachePointingByIndexStream extends MessageStream<PostfetchMyCachePointingByIndexRequest>
{

	public PostfetchMyCachePointingByIndexStream(ObjectOutputStream out, Lock lock, PostfetchMyCachePointingByIndexRequest message)
	{
		super(out, lock, message);
	}

}
