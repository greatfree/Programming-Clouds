package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckSalesRequest;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.chat.message.cs.business.CheckSalesStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 12/22/2017, Bing Li
public class CheckSalesThreadCreator implements RequestQueueCreator<CheckSalesRequest, CheckSalesStream, CheckSalesResponse, CheckSalesThread>
{

	@Override
	public CheckSalesThread createInstance(int taskSize)
	{
		return new CheckSalesThread(taskSize);
	}

}
