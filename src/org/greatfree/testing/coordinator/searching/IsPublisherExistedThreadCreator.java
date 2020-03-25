package org.greatfree.testing.coordinator.searching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.testing.message.IsPublisherExistedRequest;
import org.greatfree.testing.message.IsPublisherExistedResponse;
import org.greatfree.testing.message.IsPublisherExistedStream;

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
