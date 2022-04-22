package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysStream extends MessageStream<PostfetchMyDataByKeysRequest>
{

	public PostfetchMyDataByKeysStream(ObjectOutputStream out, Lock lock, PostfetchMyDataByKeysRequest message)
	{
		super(out, lock, message);
	}

}
