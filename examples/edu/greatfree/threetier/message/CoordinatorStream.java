package edu.greatfree.threetier.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/07/2018, Bing Li
public class CoordinatorStream extends OutMessageStream<CoordinatorRequest>
{

	public CoordinatorStream(ObjectOutputStream out, Lock lock, CoordinatorRequest message)
	{
		super(out, lock, message);
	}

}
