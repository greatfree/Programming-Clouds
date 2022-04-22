package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListStream extends MessageStream<PointingByIndexPrefetchListRequest>
{

	public PointingByIndexPrefetchListStream(ObjectOutputStream out, Lock lock, PointingByIndexPrefetchListRequest message)
	{
		super(out, lock, message);
	}

}
