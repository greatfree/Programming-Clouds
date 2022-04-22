package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataStream extends MessageStream<PostfetchMuchMyStoreDataRequest>
{

	public PostfetchMuchMyStoreDataStream(ObjectOutputStream out, Lock lock, PostfetchMuchMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
