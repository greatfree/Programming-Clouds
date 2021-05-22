package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyPointingsStream;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsThreadCreator implements RequestQueueCreator<LoadTopMyPointingsRequest, LoadTopMyPointingsStream, LoadTopMyPointingsResponse, LoadTopMyPointingsThread>
{

	@Override
	public LoadTopMyPointingsThread createInstance(int taskSize)
	{
		return new LoadTopMyPointingsThread(taskSize);
	}

}
