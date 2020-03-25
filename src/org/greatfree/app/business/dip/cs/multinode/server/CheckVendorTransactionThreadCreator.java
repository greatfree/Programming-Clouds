package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.CheckVendorTransactionRequest;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionResponse;
import org.greatfree.chat.message.cs.business.CheckVendorTransactionStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 12/20/2017, Bing Li
public class CheckVendorTransactionThreadCreator implements RequestThreadCreatable<CheckVendorTransactionRequest, CheckVendorTransactionStream, CheckVendorTransactionResponse, CheckVendorTransactionThread>
{

	@Override
	public CheckVendorTransactionThread createRequestThreadInstance(int taskSize)
	{
		return new CheckVendorTransactionThread(taskSize);
	}

}
