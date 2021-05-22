package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataStream extends MessageStream<PostfetchMyDataRequest>
{

	public PostfetchMyDataStream(ObjectOutputStream out, Lock lock, PostfetchMyDataRequest message)
	{
		super(out, lock, message);
	}

}
