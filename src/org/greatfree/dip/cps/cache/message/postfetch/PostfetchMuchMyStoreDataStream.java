package org.greatfree.dip.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataStream extends OutMessageStream<PostfetchMuchMyStoreDataRequest>
{

	public PostfetchMuchMyStoreDataStream(ObjectOutputStream out, Lock lock, PostfetchMuchMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
