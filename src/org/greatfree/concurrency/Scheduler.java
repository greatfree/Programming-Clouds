package org.greatfree.concurrency;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * The scheduler aims to schedule periodical tasks in a process. It is implemented as a singleton since it serves an entire process. All of the periodical tasks are assumed to be submitted to it. Developers can determine to use it or not themselves. They can also initialize multiple instances of ScheduledThreadPoolExecutor for their specific cases. 02/01/2016, Bing Li
 */

// Created: 02/01/2016, Bing Li
public class Scheduler
{
	// Declare the instance of ScheduledThreadPoolExecutor. 02/01/2016, Bing Li
	private ScheduledThreadPoolExecutor scheduler;

	/*
	 * The singleton implementation. 02/01/2016, Bing Li
	 */
	private Scheduler()
	{
	}

	private static Scheduler instance = new Scheduler();
	
	public static Scheduler GREATFREE()
	{
		if (instance == null)
		{
			instance = new Scheduler();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Shut down the scheduler. 02/01/2016, Bing Li
	 */
	public synchronized void shutdown(long timeout) throws InterruptedException
	{
		if (!this.scheduler.isShutdown())
		{
			this.scheduler.shutdownNow();
			this.scheduler.awaitTermination(timeout, TimeUnit.MICROSECONDS);
		}
	}

	/*
	 * Initialize the scheduler. 02/01/2016, Bing Li
	 */
	public synchronized void init(int poolSize, long keepAliveTime)
	{
		if (this.scheduler == null)
		{
			// Set the pool size. 02/01/2016, Bing Li
			this.scheduler = new ScheduledThreadPoolExecutor(poolSize);
			// The the lasted time to keep a thread alive. 02/01/2016, Bing Li
			this.scheduler.setKeepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS);
			// Set the core thread's timeout. When no tasks are available the relevant threads need to be collected and killed. 02/01/2016, Bing Li
			this.scheduler.allowCoreThreadTimeOut(true);
		}
	}

	/*
	 * Submit a new periodical task.
	 * 
	 * The variable, delay, represents the time to wait after the task is submit.
	 * 
	 * The variable, period, represents the period to execute the task.
	 * 
	 * 02/01/2016, Bing Li
	 * 
	 */
	public ScheduledFuture<?> submit(Runnable task, long delay, long period)
	{
		return this.scheduler.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
	}

	/*
	 * Cancel one submitted task. 02/01/2016, Bing Li
	 */
	public void cancel(ScheduledFuture<?> task)
	{
		task.cancel(true);
	}

	/*
	 * Return the scheduler instance if needed. 02/01/2016, Bing Li
	 */
	public ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}
}
