package com.greatfree.concurrency;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;

/*
 * This is the base of a server message dispatcher. All of the messages sent to the server are dispatched by the class concurrently. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
public class ServerMessageDispatcher<Message extends ServerMessage> implements Dispatchable<OutMessageStream<ServerMessage>>
{
	// Declare an instance of ThreadPool that is used to execute threads concurrently. 11/07/2014, Bing Li
	private ThreadPool pool;

	/*
	 * Initialize the dispatcher. 11/07/2014, Bing Li
	 */
	public ServerMessageDispatcher(int corePoolSize, long keepAliveTime)
	{
		this.pool = new ThreadPool(corePoolSize, keepAliveTime);
	}

	/*
	 * The empty implementation to process messages. 11/07/2014, Bing Li
	 */
	@Override
	public void consume(OutMessageStream<ServerMessage> msg)
	{
	}

	/*
	 * The empty implementation to shutdown the dispatcher. 11/07/2014, Bing Li
	 */
	@Override
	public void shutdown()
	{
		this.pool.shutdown();
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
	@Override
	public void execute(Thread thread)
	{
	}
}
