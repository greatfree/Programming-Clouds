package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexStream extends MessageStream<PostfetchMyPointingByIndexRequest>
{

	public PostfetchMyPointingByIndexStream(ObjectOutputStream out, Lock lock, PostfetchMyPointingByIndexRequest message)
	{
		super(out, lock, message);
	}

}
