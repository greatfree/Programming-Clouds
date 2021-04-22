package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysStream;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysThreadCreator implements RequestThreadCreatable<LoadMapStoreDataKeysRequest, LoadMapStoreDataKeysStream, LoadMapStoreDataKeysResponse, LoadMapStoreDataKeysThread>
{

	@Override
	public LoadMapStoreDataKeysThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMapStoreDataKeysThread(taskSize);
	}

}
