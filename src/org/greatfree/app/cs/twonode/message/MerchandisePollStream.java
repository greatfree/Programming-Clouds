package org.greatfree.app.cs.twonode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/31/2018, Bing Li
public class MerchandisePollStream extends MessageStream<MerchandisePollRequest>
{

	public MerchandisePollStream(ObjectOutputStream out, Lock lock, MerchandisePollRequest message)
	{
		super(out, lock, message);
	}

}
