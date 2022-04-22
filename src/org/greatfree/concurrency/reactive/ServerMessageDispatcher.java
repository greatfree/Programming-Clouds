package org.greatfree.concurrency.reactive;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;

/*
 * This is the base of a server message dispatcher. All of the messages sent to the server are dispatched by the class concurrently. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
//public abstract class ServerMessageDispatcher<Message extends ServerMessage> implements Dispatchable<OutMessageStream<ServerMessage>>
public abstract class ServerMessageDispatcher<Message extends ServerMessage>
{
	// Declare an instance of ThreadPool that is used to execute threads concurrently. 11/07/2014, Bing Li
	private ThreadPool pool;

	private ScheduledThreadPoolExecutor scheduler;
	/*
	 * Initialize the dispatcher. 11/07/2014, Bing Li
	 */
	public ServerMessageDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		this.pool = new ThreadPool(threadPoolSize, threadKeepAliveTime);
		// Set the pool size. 02/01/2016, Bing Li
		this.scheduler = new ScheduledThreadPoolExecutor(schedulerPoolSize);
		// The the lasted time to keep a thread alive. 02/01/2016, Bing Li
		this.scheduler.setKeepAliveTime(schedulerKeepAliveTime, TimeUnit.MILLISECONDS);
		// Set the core thread's timeout. When no tasks are available the relevant threads need to be collected and killed. 02/01/2016, Bing Li
		this.scheduler.allowCoreThreadTimeOut(true);
	}

	/*
	 * The empty implementation to process messages. 11/07/2014, Bing Li
	 */
	/*
	@Override
	public void consume(OutMessageStream<ServerMessage> msg)
	{
	}
	*/
	
	public abstract void consume(MessageStream<ServerMessage> msg);
//	public abstract void dispose(long timeout);

	/*
	 * Return the scheduler instance if needed. 02/01/2016, Bing Li
	 */
	public ScheduledThreadPoolExecutor getSchedulerPool()
	{
		return this.scheduler;
	}

	/*
	 * The empty implementation to shutdown the dispatcher. 11/07/2014, Bing Li
	 */
//	@Override
	public void shutdown(long timeout) throws InterruptedException
	{
		this.pool.shutdown(timeout);
		this.scheduler.shutdownNow();
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
//	@Override
	public void execute(Thread thread)
	{
		this.pool.execute(thread);
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
//	@Override
	public void execute(Runnable thread)
	{
		this.pool.execute(thread);
	}
}
