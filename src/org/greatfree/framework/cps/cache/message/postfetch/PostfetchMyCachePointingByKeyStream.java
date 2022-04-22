package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyStream extends MessageStream<PostfetchMyCachePointingByKeyRequest>
{

	public PostfetchMyCachePointingByKeyStream(ObjectOutputStream out, Lock lock, PostfetchMyCachePointingByKeyRequest message)
	{
		super(out, lock, message);
	}

}
