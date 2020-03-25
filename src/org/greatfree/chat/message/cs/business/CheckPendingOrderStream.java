package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/15/2017, Bing Li
public class CheckPendingOrderStream extends OutMessageStream<CheckPendingOrderRequest>
{

	public CheckPendingOrderStream(ObjectOutputStream out, Lock lock, CheckPendingOrderRequest message)
	{
		super(out, lock, message);
	}

}
