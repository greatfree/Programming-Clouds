package ca.multicast.search.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 03/14/2020, Bing Li
public class ClientSearchQueryStream extends OutMessageStream<ClientSearchQueryRequest>
{

	public ClientSearchQueryStream(ObjectOutputStream out, Lock lock, ClientSearchQueryRequest message)
	{
		super(out, lock, message);
	}

}
