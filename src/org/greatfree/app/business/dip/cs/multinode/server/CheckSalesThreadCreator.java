package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckSalesRequest;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.chat.message.cs.business.CheckSalesStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 12/22/2017, Bing Li
public class CheckSalesThreadCreator implements RequestThreadCreatable<CheckSalesRequest, CheckSalesStream, CheckSalesResponse, CheckSalesThread>
{

	@Override
	public CheckSalesThread createRequestThreadInstance(int taskSize)
	{
		return new CheckSalesThread(taskSize);
	}

}
