package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/07/2017, Bing Li
public class PlaceOrderStream extends OutMessageStream<PlaceOrderNotification>
{

	public PlaceOrderStream(ObjectOutputStream out, Lock lock, PlaceOrderNotification message)
	{
		super(out, lock, message);
	}

}
