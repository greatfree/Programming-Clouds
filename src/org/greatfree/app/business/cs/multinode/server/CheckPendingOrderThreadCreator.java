package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckPendingOrderRequest;
import org.greatfree.chat.message.cs.business.CheckPendingOrderResponse;
import org.greatfree.chat.message.cs.business.CheckPendingOrderStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 12/15/2017, Bing Li
public class CheckPendingOrderThreadCreator implements RequestQueueCreator<CheckPendingOrderRequest, CheckPendingOrderStream, CheckPendingOrderResponse, CheckPendingOrderThread>
{

	@Override
	public CheckPendingOrderThread createInstance(int taskSize)
	{
		return new CheckPendingOrderThread(taskSize);
	}

}
