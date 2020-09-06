package org.greatfree.dsf.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingStream extends OutMessageStream<PostfetchMinMyPointingRequest>
{

	public PostfetchMinMyPointingStream(ObjectOutputStream out, Lock lock, PostfetchMinMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
