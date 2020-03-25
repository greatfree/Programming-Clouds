package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/20/2017, Bing Li
public class CheckVendorTransactionStream extends OutMessageStream<CheckVendorTransactionRequest>
{

	public CheckVendorTransactionStream(ObjectOutputStream out, Lock lock, CheckVendorTransactionRequest message)
	{
		super(out, lock, message);
	}

}
