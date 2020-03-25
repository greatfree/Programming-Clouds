package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsStream;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsThreadCreator implements RequestThreadCreatable<LoadTopMyPointingsRequest, LoadTopMyPointingsStream, LoadTopMyPointingsResponse, LoadTopMyPointingsThread>
{

	@Override
	public LoadTopMyPointingsThread createRequestThreadInstance(int taskSize)
	{
		return new LoadTopMyPointingsThread(taskSize);
	}

}
