package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.LoadMyDataByKeysRequest;
import org.greatfree.dsf.cps.cache.message.front.LoadMyDataByKeysResponse;
import org.greatfree.dsf.cps.cache.message.front.LoadMyDataByKeysStream;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysThreadCreator implements RequestThreadCreatable<LoadMyDataByKeysRequest, LoadMyDataByKeysStream, LoadMyDataByKeysResponse, LoadMyDataByKeysThread>
{

	@Override
	public LoadMyDataByKeysThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyDataByKeysThread(taskSize);
	}

}
