package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckPendingOrderRequest;
import org.greatfree.chat.message.cs.business.CheckPendingOrderResponse;
import org.greatfree.chat.message.cs.business.CheckPendingOrderStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 12/15/2017, Bing Li
public class CheckPendingOrderThreadCreator implements RequestThreadCreatable<CheckPendingOrderRequest, CheckPendingOrderStream, CheckPendingOrderResponse, CheckPendingOrderThread>
{

	@Override
	public CheckPendingOrderThread createRequestThreadInstance(int taskSize)
	{
		return new CheckPendingOrderThread(taskSize);
	}

}
