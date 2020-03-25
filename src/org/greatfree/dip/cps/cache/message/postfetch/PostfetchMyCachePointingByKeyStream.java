package org.greatfree.dip.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyStream extends OutMessageStream<PostfetchMyCachePointingByKeyRequest>
{

	public PostfetchMyCachePointingByKeyStream(ObjectOutputStream out, Lock lock, PostfetchMyCachePointingByKeyRequest message)
	{
		super(out, lock, message);
	}

}
