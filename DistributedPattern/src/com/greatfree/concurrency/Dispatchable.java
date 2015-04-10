package com.greatfree.concurrency;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;

/*
 * It defines some interfaces that are needed in ServerMessageDispatcher. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public interface Dispatchable<Message extends OutMessageStream<ServerMessage>>
{
	// The interface to shutdown the dispatcher. 11/07/2014, Bing Li
	public void shutdown();
	// The interface to process the received messages concurrently. 11/07/2014, Bing Li
	public void consume(Message msg);
	// Start a thread. 11/07/2014, Bing Li
	public void execute(Thread thread);
}
