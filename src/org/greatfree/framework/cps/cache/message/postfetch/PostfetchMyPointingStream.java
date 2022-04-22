package org.greatfree.framework.cps.cache.message.postfetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingStream extends MessageStream<PostfetchMyPointingByKeyRequest>
{

	public PostfetchMyPointingStream(ObjectOutputStream out, Lock lock, PostfetchMyPointingByKeyRequest message)
	{
		super(out, lock, message);
	}

}
