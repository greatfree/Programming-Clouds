package org.greatfree.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.concurrency.ThreadPool;

/*
 * The class aims to send notifications asynchronously and synchronously. 04/29/2017, Bing Li
 */

// Created: 04/29/2017, Bing Li
class Client
{
	// As a singleton in a process, it should avoid the Client to be initialized more than once. The flag indicates whether the class is initialized or not. 04/30/2017, Bing Li
	private AtomicBoolean isInitialized;
	
	// An instance of FreeClientPool to manages TCP clients. 04/17/2017, Bing Li
	private FreeClientPool clientPool;
	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool threadPool;
	
	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private Client()
	{
		this.isInitialized = new AtomicBoolean(false);
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static Client instance = new Client();
	
	public static Client REMOTE()
	{
		if (instance == null)
		{
			instance = new Client();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the client eventer. 04/29/2017, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException
	{
		this.clientPool.dispose();
		this.threadPool.shutdown(timeout);
	}

	/*
	 * Initialize the client eventer. 11/07/2014, Bing Li
	 */
	public void init(int clientSize, long idleCheckDelay, long idleCheckPeriod, long maxIdleTime, int threadPoolSize, long threadKeepAliveTime)
	{
		if (!this.isInitialized.get())
		{
			this.clientPool = new FreeClientPool(clientSize);
			this.clientPool.setIdleChecker(idleCheckDelay, idleCheckPeriod, maxIdleTime);
			this.threadPool = new ThreadPool(threadPoolSize, threadKeepAliveTime);
			this.isInitialized.set(true);
		}
	}

	/*
	 * Expose the client pool. 04/29/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}

	/*
	 * Expose the thread pool. 04/29/2017, Bing Li
	 */
	public ThreadPool getThreadPool()
	{
		return this.threadPool;
	}
}
