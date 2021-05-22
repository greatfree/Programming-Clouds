package org.greatfree.app.cs.twonode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/27/2018, Bing Li
public class MerchandiseStream extends MessageStream<MerchandiseRequest>
{

	public MerchandiseStream(ObjectOutputStream out, Lock lock, MerchandiseRequest message)
	{
		super(out, lock, message);
	}

}
