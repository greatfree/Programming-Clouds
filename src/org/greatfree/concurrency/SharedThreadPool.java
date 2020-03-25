package org.greatfree.concurrency;

/*
 * This is a singleton implementation of ThreadPool. It is usually employed in the case that does not need a lot of threads. So, it can be shared by a couple of classes. 02/27/2016, Bing Li
 */

// Created: 02/27/2016, Bing Li
public class SharedThreadPool
{
	// Declared an instance of ThreadPool. 02/27/2016, Bing Li
	private ThreadPool pool;

	/*
	 * The singleton implementation of ThreadPool. 02/27/2016, Bing Li
	 */
	private SharedThreadPool()
	{
	}
	
	private static SharedThreadPool instance = new SharedThreadPool();
	
	public static SharedThreadPool SHARED()
	{
		if (instance == null)
		{
			instance = new SharedThreadPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the shared thread pool. 02/27/2016, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException
	{
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize the thread pool. 02/27/2016, Bing Li
	 */
	public synchronized void init(int corePoolSize, long keepAliveTime)
	{
		if (this.pool == null)
		{
			this.pool = new ThreadPool(corePoolSize, keepAliveTime);
		}
	}

	/*
	 * Expose the thread pool for sharing. 02/27/2016, Bing Li
	 */
	public ThreadPool getPool()
	{
		return this.pool;
	}
}
