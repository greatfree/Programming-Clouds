package org.greatfree.dip.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValuesStream extends OutMessageStream<PostfetchMyUKValuesRequest>
{

	public PostfetchMyUKValuesStream(ObjectOutputStream out, Lock lock, PostfetchMyUKValuesRequest message)
	{
		super(out, lock, message);
	}

}
