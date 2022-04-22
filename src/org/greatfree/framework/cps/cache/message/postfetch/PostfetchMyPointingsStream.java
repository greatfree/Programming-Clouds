package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingsStream extends MessageStream<PostfetchMyPointingsRequest>
{

	public PostfetchMyPointingsStream(ObjectOutputStream out, Lock lock, PostfetchMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
