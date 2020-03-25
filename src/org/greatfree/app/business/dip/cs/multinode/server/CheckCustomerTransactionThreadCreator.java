package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckCustomerTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckCustomerTransactionStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 12/20/2017, Bing Li
public class CheckCustomerTransactionThreadCreator implements RequestThreadCreatable<CheckCustomerTransactionRequest, CheckCustomerTransactionStream, CheckCustomerTransactionResponse, CheckCustomerTransactionThread>
{

	@Override
	public CheckCustomerTransactionThread createRequestThreadInstance(int taskSize)
	{
		return new CheckCustomerTransactionThread(taskSize);
	}

}
