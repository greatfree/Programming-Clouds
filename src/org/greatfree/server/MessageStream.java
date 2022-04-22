package org.greatfree.server;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.message.ServerMessage;

/*
 * The class consists of the output stream that responds a client. The lock is used to keep responding operations atomic. The request is any message that extends ServerMessage. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
public class MessageStream<Message extends ServerMessage>
{
	// The output stream that is responsible for responding clients. 07/30/2014, Bing Li
	private ObjectOutputStream out;
	// The lock that keeps output operations atomic. 07/30/2014, Bing Li
	private Lock lock;
	// The message that extends ServerMessage. 07/30/2014, Bing Li
	private Message message;
	
	public MessageStream(ObjectOutputStream out, Lock lock, Message message)
	{
		// The output stream is shared by different threads that respond the same client concurrently. 07/30/2014, Bing Li
		this.out = out;
		// One client is assigned a unique lock such that responding to the client is not affected by concurrency. 07/30/2014, Bing Li 
		this.lock = lock;
		// The request from the client. 07/30/2014, Bing Li
		this.message = message;
	}

	/*
	 * Expose the output stream. 07/30/2014, Bing Li
	 */
	public ObjectOutputStream getOutStream()
	{
		return this.out;
	}

	/*
	 * Expose the lock. 07/30/2014, Bing Li
	 */
	public Lock getLock()
	{
		return this.lock;
	}

	/*
	 * Expose the request. 07/30/2014, Bing Li
	 */
	public Message getMessage()
	{
		return this.message;
	}

	/*
	 * Dispose the request after the responding is done. 07/30/2014, Bing Li
	 */
	public void disposeMessage()
	{
		this.message = null;
	}
}
