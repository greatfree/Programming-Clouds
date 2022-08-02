package org.greatfree.concurrency;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.util.Tools;

/*
 * The server key is updated to be identical to the one of CSServer. 03/30/2020, Bing Li
 * 
 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
 * 
 * If only one server container exists in a process, the key is not required to be identical to the one of CSServer. It is generated arbitrarily. 03/30/2020, Bing Li
 */

/*
 * This class contains the basic parameters to initialize dispatchers, which are widely used in the development environment to manage threads efficiently. 12/01/2016, Bing Li
 */

// Created: 12/01/2016, Bing Li
public abstract class ConcurrentDispatcher implements Runnable, IdleCheckable
{
	// The key of the dispatcher. 12/01/2016, Bing Li
	private final String serverKey;
	// The size of the thread pool. 05/19/2018, Bing Li
	private final int poolSize;
	// Declare a thread pool that is used to run a thread. 11/04/2014, Bing Li
//	private ThreadPool threadPool;
	// Declare the maximum task length for each thread to be created. // 11/04/2014, Bing Li
	private final int maxTaskSizePerThread;
	// Declare the maximum thread count that can be created in the dispatcher. // 11/04/2014, Bing Li
//	private final int maxThreadSize;
	// The timer is replaced because its tasks cannot be canceled. The self-disposing mechanism needs a re-submission approach to do that. Thus, the ScheduledThreadPoolExecutor is selected. 02/01/2016, Bing Li
	private ScheduledThreadPoolExecutor scheduler;
	// The collaborator is used to pause the dispatcher when no messages are available and notify to continue when new messages are received. 11/04/2014, Bing Li
	private Sync workCollaborator;
	// The time to wait when no messages are available. 11/04/2014, Bing Li
	private final long dispatcherWaitTime;
	// A flag that indicates whether a local thread pool is used or a global one is used. Since it might get problems if the global pool is shutdown inside the class. Thus, it is required to set the flag. 11/19/2014, Bing Li
//	private final boolean isSelfThreadPool;
	// The delay time before checking the idle state. 01/14/2016, Bing Li
	private final long idleCheckDelay;
	// The idle-checking period. 01/14/2016, Bing Li
	private final long idleCheckPeriod;
	// The count of the loop when no tasks are available to be processed. When the value is exceeded, the dispatcher is killed to save resources. 01/14/2016, Bing Li
//	private final int waitRound;
	// The timeout to shut down the self-managed thread pool. 04/19/2018, Bing Li
//	private long timeout;

	/*
	 * Initialize the dispatcher. 12/01/2016, Bing Li
	 */
	/*
	public ConcurrentDispatcher(int poolSize, long keepAliveTime, int maxTaskSize, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, boolean isSelfThreadPool, long idleCheckDelay, long idleCheckPeriod, int waitRound, long timeout)
	{
		// Generate a unique key for each dispatcher. 12/01/2016, Bing Li
		this.key = Tools.generateUniqueKey();
		this.threadPool = new ThreadPool(poolSize, keepAliveTime);
		this.maxTaskSize = maxTaskSize;
		this.maxThreadSize = poolSize;
		this.scheduler = scheduler;
		this.workCollaborator = new Sync(true);
		this.dispatcherWaitTime = dispatcherWaitTime;
		this.isSelfThreadPool = isSelfThreadPool;
		this.waitRound = waitRound;
		this.idleCheckDelay = idleCheckDelay;
		this.idleCheckPeriod = idleCheckPeriod;
		this.timeout = timeout;
	}
	*/

//	public ConcurrentDispatcher(ThreadPool threadPool, int maxTaskSize, int poolSize, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod, int waitRound)
//	public ConcurrentDispatcher(ThreadPool threadPool, int maxTaskSize, int poolSize, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, boolean isSelfThreadPool, long idleCheckDelay, long idleCheckPeriod, int waitRound, long timeout)
//	public ConcurrentDispatcher(ThreadPool threadPool, int maxTaskSize, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod, int waitRound)
//	public ConcurrentDispatcher(int poolSize, int maxTaskSizePerThread, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod, int waitRound)
	public ConcurrentDispatcher(int poolSize, int maxTaskSizePerThread, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod)
	{
		// Generate a unique key for each dispatcher. 12/01/2016, Bing Li
		this.serverKey = Tools.generateUniqueKey();
		this.poolSize = poolSize;
//		this.threadPool = threadPool;
		this.maxTaskSizePerThread = maxTaskSizePerThread;
//		this.maxThreadSize = poolSize;
		this.scheduler = scheduler;
		this.workCollaborator = new Sync(true);
		this.dispatcherWaitTime = dispatcherWaitTime;
//		this.isSelfThreadPool = isSelfThreadPool;
//		this.waitRound = waitRound;
		this.idleCheckDelay = idleCheckDelay;
		this.idleCheckPeriod = idleCheckPeriod;
//		this.timeout = timeout;
	}

//	public ConcurrentDispatcher(String serverKey, int poolSize, int maxTaskSizePerThread, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod, int waitRound)
	public ConcurrentDispatcher(String serverKey, int poolSize, int maxTaskSizePerThread, ScheduledThreadPoolExecutor scheduler, long dispatcherWaitTime, long idleCheckDelay, long idleCheckPeriod)
	{
		/*
		 * The server key is updated to be identical to the one of CSServer. 03/30/2020, Bing Li
		 * 
		 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
		 * 
		 * If only one server container exists in a process, the key is not required to be identical to the one of CSServer. It is generated arbitrarily. 03/30/2020, Bing Li
		 */
		if (serverKey != null)
		{
			this.serverKey = serverKey;
		}
		else
		{
			this.serverKey = Tools.generateUniqueKey();
		}
		this.poolSize = poolSize;
//		this.threadPool = threadPool;
		this.maxTaskSizePerThread = maxTaskSizePerThread;
//		this.maxThreadSize = poolSize;
		this.scheduler = scheduler;
		this.workCollaborator = new Sync(true);
		this.dispatcherWaitTime = dispatcherWaitTime;
//		this.isSelfThreadPool = isSelfThreadPool;
//		this.waitRound = waitRound;
		this.idleCheckDelay = idleCheckDelay;
		this.idleCheckPeriod = idleCheckPeriod;
//		this.timeout = timeout;
	}

	protected abstract void dispose() throws InterruptedException;
	protected abstract boolean isReady();

	/*
	 * Expose the key. 12/01/2016, Bing Li
	 */
	protected String getServerKey()
	{
		return this.serverKey;
	}

	/*
	 * Reset the thread pool. 12/01/2016, Bing Li
	 */
	/*
	public void resetThreadPool()
	{
		this.threadPool.reset();
	}
	*/
	
	/*
	 * Shut down the thread pool. 12/01/2016, Bing Li
	 */
	/*
	public void shutdownThreadPool(long timeout) throws InterruptedException
	{
		this.threadPool.shutdown(timeout);
	}
	*/

	/*
	 * Execute a thread. 12/01/2016, Bing Li
	 */
	/*
	public void execute(Thread thread)
	{
		this.threadPool.execute(thread);
	}
	*/

	/*
	 * Execute a task. 12/01/2016, Bing Li
	 */
	/*
	public void execute(Runnable thread)
	{
		this.threadPool.execute(thread);
	}
	*/
	
	protected int getPoolSize()
	{
		return this.poolSize;
	}

	/*
	 * Expose the maximum task size of a thread queue. 12/01/2016, Bing Li
	 */
	protected int getMaxTaskSizePerThread()
	{
		return this.maxTaskSizePerThread;
	}

	/*
	 * Expose the maximum size of the threads which can be alive in the dispatcher. 12/01/2016, Bing Li
	 */
	/*
	public int getMaxThreadSize()
	{
		return this.threadPool.getCorePoolSize();
	}
	*/

	/*
	 * Expose the scheduler. 12/01/2016, Bing Li
	 */
	protected ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}

	/*
	 * 
	 */
	protected void shutdownSync()
	{
		this.workCollaborator.shutdown();
	}
	
	protected void resetSync()
	{
		this.workCollaborator.reset();
	}
	
	protected void signal()
	{
		this.workCollaborator.signalAll();
	}
	
	protected boolean holdOn()
	{
		return this.workCollaborator.holdOn(this.dispatcherWaitTime);
	}
	
	protected boolean isShutdown()
	{
		return this.workCollaborator.isShutdown();
	}

	/*
	public boolean isSelfThreadPool()
	{
		return this.isSelfThreadPool;
	}
	*/
	
	protected long getIdleCheckDelay()
	{
		return this.idleCheckDelay;
	}
	
	protected long getIdleCheckPeriod()
	{
		return this.idleCheckPeriod;
	}

	/*
	protected long getWaitRound()
	{
		return this.waitRound;
	}
	*/

	/*
	public long getTimeout()
	{
		return this.timeout;
	}
	*/
}
