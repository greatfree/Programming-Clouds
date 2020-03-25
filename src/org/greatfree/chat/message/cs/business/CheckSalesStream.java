package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/22/2017, Bing Li
public class CheckSalesStream extends OutMessageStream<CheckSalesRequest>
{

	public CheckSalesStream(ObjectOutputStream out, Lock lock, CheckSalesRequest message)
	{
		super(out, lock, message);
	}

}
