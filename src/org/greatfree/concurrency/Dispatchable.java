package org.greatfree.concurrency;

import org.greatfree.client.MessageStream;
import org.greatfree.message.ServerMessage;

/*
 * It defines some interfaces that are needed in ServerMessageDispatcher. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public interface Dispatchable<Message extends MessageStream<ServerMessage>>
{
	// The interface to shutdown the dispatcher. 11/07/2014, Bing Li
	public void shutdown(long timeout) throws InterruptedException;
	// The interface to process the received messages concurrently. 11/07/2014, Bing Li
	public void consume(Message msg);
	// Start a thread. 11/07/2014, Bing Li
	public void execute(Thread thread);
	// Start a thread. 11/07/2014, Bing Li
	public void execute(Runnable thread);
}
