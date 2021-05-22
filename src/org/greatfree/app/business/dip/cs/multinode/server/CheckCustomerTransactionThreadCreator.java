package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckCustomerTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 12/20/2017, Bing Li
public class CheckCustomerTransactionThreadCreator implements RequestQueueCreator<CheckCustomerTransactionRequest, CheckCustomerTransactionStream, CheckCustomerTransactionResponse, CheckCustomerTransactionThread>
{

	@Override
	public CheckCustomerTransactionThread createInstance(int taskSize)
	{
		return new CheckCustomerTransactionThread(taskSize);
	}

}
