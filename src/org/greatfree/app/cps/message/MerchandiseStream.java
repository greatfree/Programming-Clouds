package org.greatfree.app.cps.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/14/2018, Bing Li
public class MerchandiseStream extends OutMessageStream<MerchandiseRequest>
{

	public MerchandiseStream(ObjectOutputStream out, Lock lock, MerchandiseRequest message)
	{
		super(out, lock, message);
	}

}
