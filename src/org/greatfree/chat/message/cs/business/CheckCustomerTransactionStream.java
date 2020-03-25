package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/20/2017, Bing Li
public class CheckCustomerTransactionStream extends OutMessageStream<CheckCustomerTransactionRequest>
{

	public CheckCustomerTransactionStream(ObjectOutputStream out, Lock lock, CheckCustomerTransactionRequest message)
	{
		super(out, lock, message);
	}

}
