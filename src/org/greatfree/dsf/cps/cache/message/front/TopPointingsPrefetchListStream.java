package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListStream extends OutMessageStream<TopPointingsPrefetchListRequest>
{

	public TopPointingsPrefetchListStream(ObjectOutputStream out, Lock lock, TopPointingsPrefetchListRequest message)
	{
		super(out, lock, message);
	}

}
