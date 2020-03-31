package ca.dp.mncs.circle.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/25/2020, Bing Li
public class PollPostStream extends OutMessageStream<PollPostRequest>
{

	public PollPostStream(ObjectOutputStream out, Lock lock, PollPostRequest message)
	{
		super(out, lock, message);
	}

}
