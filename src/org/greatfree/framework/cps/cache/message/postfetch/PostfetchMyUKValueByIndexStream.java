package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValueByIndexStream extends MessageStream<PostfetchMyUKValueByIndexRequest>
{

	public PostfetchMyUKValueByIndexStream(ObjectOutputStream out, Lock lock, PostfetchMyUKValueByIndexRequest message)
	{
		super(out, lock, message);
	}

}
