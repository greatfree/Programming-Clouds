package ca.streaming.news.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/05/2020, Bing Li
public class IsVideoExistedStream extends OutMessageStream<IsVideoExistedRequest>
{

	public IsVideoExistedStream(ObjectOutputStream out, Lock lock, IsVideoExistedRequest message)
	{
		super(out, lock, message);
	}

}
