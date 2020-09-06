package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.LoadRangeMyPointingsRequest;
import org.greatfree.dsf.cps.cache.message.front.LoadRangeMyPointingsResponse;
import org.greatfree.dsf.cps.cache.message.front.LoadRangeMyPointingsStream;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsThreadCreator implements RequestThreadCreatable<LoadRangeMyPointingsRequest, LoadRangeMyPointingsStream, LoadRangeMyPointingsResponse, LoadRangeMyPointingsThread>
{

	@Override
	public LoadRangeMyPointingsThread createRequestThreadInstance(int taskSize)
	{
		return new LoadRangeMyPointingsThread(taskSize);
	}

}
