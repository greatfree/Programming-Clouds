package edu.greatfree.threetier.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/06/2018, Bing Li
public class FrontStream extends OutMessageStream<FrontRequest>
{

	public FrontStream(ObjectOutputStream out, Lock lock, FrontRequest message)
	{
		super(out, lock, message);
	}

}
