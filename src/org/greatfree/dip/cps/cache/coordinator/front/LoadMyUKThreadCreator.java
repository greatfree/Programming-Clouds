package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.LoadMyUKRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyUKResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyUKStream;

// Created: 03/01/2019, Bing Li
public class LoadMyUKThreadCreator implements RequestThreadCreatable<LoadMyUKRequest, LoadMyUKStream, LoadMyUKResponse, LoadMyUKThread>
{

	@Override
	public LoadMyUKThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyUKThread(taskSize);
	}

}
