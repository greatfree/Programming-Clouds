package org.greatfree.framework.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 03/21/2020, Bing Li
public class SearchStream extends MessageStream<SearchRequest>
{

	public SearchStream(ObjectOutputStream out, Lock lock, SearchRequest message)
	{
		super(out, lock, message);
	}

}
