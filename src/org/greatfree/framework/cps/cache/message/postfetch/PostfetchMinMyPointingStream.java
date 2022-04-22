package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingStream extends MessageStream<PostfetchMinMyPointingRequest>
{

	public PostfetchMinMyPointingStream(ObjectOutputStream out, Lock lock, PostfetchMinMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
