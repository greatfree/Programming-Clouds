package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/03/2018, Bing Li
public class RangePointingsPrefetchListStream extends OutMessageStream<RangePointingsPrefetchListRequest>
{

	public RangePointingsPrefetchListStream(ObjectOutputStream out, Lock lock, RangePointingsPrefetchListRequest message)
	{
		super(out, lock, message);
	}

}
