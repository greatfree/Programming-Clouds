package org.greatfree.dip.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysStream extends OutMessageStream<PostfetchMyStoreDataKeysRequest>
{

	public PostfetchMyStoreDataKeysStream(ObjectOutputStream out, Lock lock, PostfetchMyStoreDataKeysRequest message)
	{
		super(out, lock, message);
	}

}
