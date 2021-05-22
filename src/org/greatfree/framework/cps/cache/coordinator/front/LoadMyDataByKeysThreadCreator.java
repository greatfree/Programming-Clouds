package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataByKeysRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataByKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataByKeysStream;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysThreadCreator implements RequestQueueCreator<LoadMyDataByKeysRequest, LoadMyDataByKeysStream, LoadMyDataByKeysResponse, LoadMyDataByKeysThread>
{

	@Override
	public LoadMyDataByKeysThread createInstance(int taskSize)
	{
		return new LoadMyDataByKeysThread(taskSize);
	}

}
