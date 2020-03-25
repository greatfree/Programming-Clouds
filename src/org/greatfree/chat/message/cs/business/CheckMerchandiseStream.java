package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/05/2017, Bing Li
public class CheckMerchandiseStream extends OutMessageStream<CheckMerchandiseRequest>
{

	public CheckMerchandiseStream(ObjectOutputStream out, Lock lock, CheckMerchandiseRequest message)
	{
		super(out, lock, message);
	}

}
