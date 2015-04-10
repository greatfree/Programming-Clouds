package com.greatfree.testing.coordinator.searching;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.IsPublisherExistedRequest;
import com.greatfree.testing.message.IsPublisherExistedResponse;
import com.greatfree.testing.message.IsPublisherExistedStream;

/*
 * A creator to initialize instances of IsPublisherExistedThread. It is used in the instance of RequestDispatcher. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedThreadCreator implements RequestThreadCreatable<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread>
{
	@Override
	public IsPublisherExistedThread createRequestThreadInstance(int taskSize)
	{
		return new IsPublisherExistedThread(taskSize);
	}
}
