package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyPointingsStream;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsThreadCreator implements RequestQueueCreator<LoadRangeMyPointingsRequest, LoadRangeMyPointingsStream, LoadRangeMyPointingsResponse, LoadRangeMyPointingsThread>
{

	@Override
	public LoadRangeMyPointingsThread createInstance(int taskSize)
	{
		return new LoadRangeMyPointingsThread(taskSize);
	}

}
