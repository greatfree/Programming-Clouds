package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysStream;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysThreadCreator implements RequestQueueCreator<LoadMapStoreDataKeysRequest, LoadMapStoreDataKeysStream, LoadMapStoreDataKeysResponse, LoadMapStoreDataKeysThread>
{

	@Override
	public LoadMapStoreDataKeysThread createInstance(int taskSize)
	{
		return new LoadMapStoreDataKeysThread(taskSize);
	}

}
