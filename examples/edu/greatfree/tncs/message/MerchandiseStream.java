package edu.greatfree.tncs.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/30/2019, Bing Li
public class MerchandiseStream extends OutMessageStream<MerchandiseRequest>
{
	public MerchandiseStream(ObjectOutputStream out, Lock lock, MerchandiseRequest message)
	{
		super(out, lock, message);
	}
}
