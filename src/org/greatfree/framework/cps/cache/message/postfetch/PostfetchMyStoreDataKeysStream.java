package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysStream extends MessageStream<PostfetchMyStoreDataKeysRequest>
{

	public PostfetchMyStoreDataKeysStream(ObjectOutputStream out, Lock lock, PostfetchMyStoreDataKeysRequest message)
	{
		super(out, lock, message);
	}

}
