package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListStream extends MessageStream<TopPointingsPrefetchListRequest>
{

	public TopPointingsPrefetchListStream(ObjectOutputStream out, Lock lock, TopPointingsPrefetchListRequest message)
	{
		super(out, lock, message);
	}

}
