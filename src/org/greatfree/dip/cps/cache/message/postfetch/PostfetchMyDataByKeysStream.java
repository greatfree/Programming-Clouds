package org.greatfree.dip.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysStream extends OutMessageStream<PostfetchMyDataByKeysRequest>
{

	public PostfetchMyDataByKeysStream(ObjectOutputStream out, Lock lock, PostfetchMyDataByKeysRequest message)
	{
		super(out, lock, message);
	}

}
