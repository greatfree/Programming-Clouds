package org.greatfree.app.search.multicast.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 09/28/2018, Bing Li
public class SearchStream extends MessageStream<SearchRequest>
{

	public SearchStream(ObjectOutputStream out, Lock lock, SearchRequest message)
	{
		super(out, lock, message);
	}

}
