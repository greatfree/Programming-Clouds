package ca.dp.mncs.circle.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/25/2020, Bing Li
public class PollLikeStream extends OutMessageStream<PollLikeRequest>
{

	public PollLikeStream(ObjectOutputStream out, Lock lock, PollLikeRequest message)
	{
		super(out, lock, message);
	}

}
