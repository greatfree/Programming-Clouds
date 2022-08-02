package org.greatfree.concurrency.reactive;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.ConcurrentDispatcher;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.ThreadIdleChecker;
import org.greatfree.message.abandoned.BroadcastRequest;
import org.greatfree.message.abandoned.BroadcastResponse;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.UtilConfig;

/*
 * This is an important class that enqueues requests and creates broadcast queue threads to respond them concurrently. If the current host does not contain the requested data, it is necessary to forward the request to the host's children. 11/29/2014, Bing Li
 * 
 * It works in the way like a scheduler or a dispatcher. That is why it is named. 11/29/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * One important update is that the ConcurrentDispatcher is designed. It works as a parent class that must be derived by all of the dispatchers.
 * 
 * The monitorLock, from my point of view, is not needed. It is removed in the version. 12/03/2016, Bing Li
 * 
 */

// Created: 11/29/2014, Bing Li
// public abstract class BroadcastRequestDispatcher<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestThread extends BroadcastRequestQueue<Request, Response>, RequestThreadCreator extends BroadcastRequestThreadCreatable<Request, Response, RequestThread>> extends ConcurrentDispatcher implements Runnable, CheckIdleable
public abstract class BroadcastRequestDispatcher<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestThread extends BroadcastRequestQueue<Request, Response>, RequestThreadCreator extends BroadcastRequestThreadCreatable<Request, Response, RequestThread>> extends ConcurrentDispatcher
{
	// Declare a map to contain all of the threads. 11/29/2014, Bing Li
	private Map<String, Runner<RequestThread>> threads;
	// Declare a queue to contain requests. 11/29/2014, Bing Li 
	private Queue<Request> requestQueue;
	// Declare a thread pool that is used to run a thread. 11/29/2014, Bing Li
	private FreeClientPool pool;
	// The IP/port is the initiator of the broadcast requesting. Keep the information to send the response to it once if the result is obtained. 11/29/2014, Bing Li
	private IPResource serverAddress;
	// Declare a thread creator that is used to initialize a thread instance. 11/29/2014, Bing Li
	private RequestThreadCreator threadCreator;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// Declare the checker to check whether created threads are idle long enough. 11/29/2014, Bing Li
	private ThreadIdleChecker<BroadcastRequestDispatcher<Request, Response, RequestThread, RequestThreadCreator>> idleChecker;

	/*
	 * Initialize the broadcast request dispatcher. The constructor is called when the thread pool is owned by the dispatcher only. 11/29/2014, Bing Li
	 */
	/*
	public BroadcastRequestDispatcher(FreeClientPool pool, String serverAddress, int serverPort, int poolSize, long keepAliveTime, RequestThreadCreator actionThreadCreator, int maxTaskSize, int maxThreadSize, long dispatcherWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, ScheduledThreadPoolExecutor scheduler, long timeout)
	{
		super(poolSize, keepAliveTime, maxTaskSize, scheduler, dispatcherWaitTime, true, idleCheckDelay, idleCheckPeriod, waitRound, timeout);
		this.pool = pool;
		this.serverAddress = new IPPort(serverAddress, serverPort);
		this.threads = new ConcurrentHashMap<String, RequestThread>();
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.idleChecker = new ThreadIdleChecker<BroadcastRequestDispatcher<Request, Response, RequestThread, RequestThreadCreator>>(this);
	}
	*/
	
	/*
	 * Dispose the broadcast request dispatcher. The constructor is called when the thread pool is shared. 11/29/2014, Bing Li
	 */
//	public BroadcastRequestDispatcher(FreeClientPool pool, String serverAddress, int serverPort, ThreadPool threadPool, RequestThreadCreator actionThreadCreator, int maxTaskSize, int maxThreadSize, long dispatcherWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, ScheduledThreadPoolExecutor scheduler, long timeout)
	public BroadcastRequestDispatcher(FreeClientPool pool, String serverAddress, int serverPort, RequestThreadCreator actionThreadCreator, int poolSize, int maxTaskSize, int maxThreadSize, long dispatcherWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, ScheduledThreadPoolExecutor scheduler, long timeout)
	{
//		super(threadPool, maxTaskSize, scheduler, dispatcherWaitTime, idleCheckDelay, idleCheckPeriod, waitRound);
//		super(poolSize, maxTaskSize, scheduler, dispatcherWaitTime, idleCheckDelay, idleCheckPeriod, waitRound);
		super(poolSize, maxTaskSize, scheduler, dispatcherWaitTime, idleCheckDelay, idleCheckPeriod);
		this.pool = pool;
		this.serverAddress = new IPResource(serverAddress, serverPort);
		this.threads = new ConcurrentHashMap<String, Runner<RequestThread>>();
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.threadCreator = actionThreadCreator;
		this.idleChecker = new ThreadIdleChecker<BroadcastRequestDispatcher<Request, Response, RequestThread, RequestThreadCreator>>(this);
	}

	/*
	 * Since the dispatcher is killed when no tasks are available for sufficient time, it needs to be restarted when new tasks need to be processed. The method is used for that. 01/13/2016, Bing Li
	 */
	private void restart()
	{
		// Reset the collaborator for the new resumption. 02/01/2016, Bing Li
		this.resetSync();
		// If the map of threads is disposed, it needs to initialize a new one. 01/13/2016, Bing Li
		if (this.threads == null)
		{
			// Create a new map to keep the threads. 01/13/2016, Bing Li
			this.threads = new ConcurrentHashMap<String, Runner<RequestThread>>();
		}

		// If the queue of requests is disposed, it needs to initialize a new one. 01/13/2016, Bing Li
		if (this.requestQueue == null)
		{
			// Create a new queue to keep the requests. 01/13/2016, Bing Li
			this.requestQueue = new LinkedBlockingQueue<Request>();
		}

		// Check whether the thread pool is self-owned. 02/02/2016, Bing Li
		/*
		if (this.isSelfThreadPool())
		{
			// Reset the thread pool if it is shut down. 01/13/2016, Bing Li
			this.resetThreadPool();
		}
		*/

		// Reset the idle checking. 01/13/2016, Bing Li
		this.setIdleChecker(this.getIdleCheckDelay(), this.getIdleCheckPeriod());
	}

	/*
	 * Dispose the request dispatcher. 11/29/2014, Bing Li
	 */
	public synchronized void dispose() throws InterruptedException
	{
		// Set the sync as shutdown. 02/26/2016, Bing Li
		this.shutdownSync();

		// Clear the request queue. 11/29/2014, Bing Li
		if (this.requestQueue != null)
		{
			this.requestQueue.clear();
		}
		// Detect whether the idle-checking task is initialized. 02/01/2016, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Cancel the idle-checking task. 02/01/2016, Bing Li
			this.idleCheckingTask.cancel(true);
		}

		// Dispose all of threads created during the dispatcher's running procedure. 11/29/2014, Bing Li
		for (Runner<RequestThread> thread : this.threads.values())
		{
			thread.stop();
		}
		// Clear the threads. 11/29/2014, Bing Li
		this.threads.clear();
		// Check whether the thread is owned by the dispatcher only. 11/29/2014, Bing Li
		/*
		if (this.isSelfThreadPool())
		{
			// Shutdown the thread pool. 11/29/2014, Bing Li
			this.shutdownThreadPool(this.getTimeout());
		}
		*/
	}

	/*
	 * Detect whether the dispatcher is ready for receiving new tasks or not. If not, it needs to restart the dispatcher. 01/13/2016, Bing Li
	 */
	public synchronized boolean isReady()
	{
		// Check whether the dispatcher is shut down. 01/14/2016, Bing Li
		if (!this.isShutdown())
		{
			// If not, it represents the dispatcher is ready. 02/01/2016, Bing Li
			return true;
		}
		else
		{
			// Restart the dispatcher. 01/14/2016, Bing Li
			this.restart();
			// If the dispatcher is shutdown, it is not ready. With the returned false value, the dispatcher needs to be executed as a thread by an outside thread pool. 02/01/2016, Bing Li
			return false;
		}
	}

	/*
	 * Set the idle checking parameters. 11/29/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		try
		{
			// Schedule the idle-checking task and obtain the task instance, which is used to cancel the task when required. 02/01/2016, Bing Li
			this.idleCheckingTask = this.getScheduler().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			// When the scheduling is rejected for shutting down the server, the exception is not necessary to be shown. 02/01/2016, Bing Li
			ServerStatus.FREE().printException(e);
		}
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of threads within the dispatcher. 11/29/2014, Bing Li
	 */
	@Override
	public void checkIdle() throws InterruptedException
	{
		// Check each thread managed by the dispatcher. 11/29/2014, Bing Li
		for (Runner<RequestThread> thread : this.threads.values())
		{
			// If the thread is empty and idle, it is the one to be checked. 11/29/2014, Bing Li
			if (thread.getFunction().isIdle())
			{
				// The algorithm to determine whether a thread should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/29/2014, Bing Li
				this.threads.remove(thread.getFunction().getKey());
				// Dispose the thread. 11/29/2014, Bing Li
				thread.stop();
				// Collect the resource of the thread. 11/29/2014, Bing Li
				thread = null;
			}
		}
	}

	/*
	 * Enqueue the newly received request into the dispatcher. 11/29/2014, Bing Li
	 */
	public synchronized void enqueue(Request request)
	{
		// Enqueue the request into the queue. 11/29/2014, Bing Li
		this.requestQueue.add(request);
		// Notify the dispatcher thread, which is possibly blocked when no requests are available, to keep working. 11/29/2014, Bing Li
		this.signal();
	}

	/*
	 * It is required to synchronize the process to create threads. It is noted that a newly created thread is not put into the map when the dispatcher is disposed. If so, the thread cannot be shutdown even when the process is shutdown. Any threads must be put into the map for management. 08/10/2016, Bing Li
	 */
//	private synchronized boolean createThread(Request request, int upperSize)
	private synchronized boolean createThread(int upperSize)
	{
		// Check whether the thread pool reaches the maximum size. 12/03/2016, Bing Li
		if (this.threads.size() < upperSize)
		{
			// Create a new thread. 11/29/2014, Bing Li
			RequestThread thread = this.threadCreator.createRequestThreadInstance(this.serverAddress, this.pool, this.getMaxTaskSizePerThread());
			// Take the request. 11/29/2014, Bing Li
			thread.enqueue(this.requestQueue.poll());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<RequestThread> runner = new Runner<RequestThread>(thread);
			// Start the thread. 11/29/2014, Bing Li
			runner.start();
			// Put it into the map for further reuse. 11/29/2014, Bing Li
			this.threads.put(thread.getKey(), runner);
			// Start the thread. 11/29/2014, Bing Li
//			this.execute(thread);
			// Return true if a new thread is created. 12/03/2016, Bing Li
			return true;
		}
		// Return false if no new thread is created. 12/03/2016, Bing Li
		return false;
	}

	/*
	 * It is required to synchronize the process to create threads. It is noted that a newly created thread is not put into the map when the dispatcher is disposed. If so, the thread cannot be shutdown even when the process is shutdown. Any threads must be put into the map for management. 08/10/2016, Bing Li
	 */
//	private synchronized boolean createThread(Request request)
	private synchronized boolean createThread()
	{
		// Check whether the thread pool is empty. 12/03/2016, Bing Li
		if (this.threads.size() <= 0)
		{
			// Create a new thread. 11/29/2014, Bing Li
			RequestThread thread = this.threadCreator.createRequestThreadInstance(this.serverAddress, this.pool, this.getMaxTaskSizePerThread());
			// Take the request. 11/29/2014, Bing Li
			thread.enqueue(this.requestQueue.poll());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<RequestThread> runner = new Runner<RequestThread>(thread);
			// Start the thread. 11/29/2014, Bing Li
			runner.start();
			// Put it into the map for further reuse. 11/29/2014, Bing Li
			this.threads.put(thread.getKey(), runner);
			// Start the thread. 11/29/2014, Bing Li
//			this.execute(thread);
			// Return true if a new thread is created. 12/03/2016, Bing Li
			return true;
		}
		// Return false if no new thread is created. 12/03/2016, Bing Li
		return false;
	}

	/*
	 * The thread of the dispatcher is always running until no requests from clients will be received. If too many requests are received, more threads are created by the dispatcher to respond users in time. If requests are limited, the count of threads created by the dispatcher is also small. It is possible no any threads are alive when no requests are received for a long time. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// Declare a request. 11/29/2014, Bing Li
//		Request request;
		// Declare a string to keep the selected thread key. 11/29/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// The value is used to count the count of loops for the dispatcher when no tasks are available. 01/13/2016, Bing Li
//		AtomicInteger currentRound = new AtomicInteger(0);
		// The dispatcher usually runs all of the time unless the server is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/29/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether requests are received and saved in the queue. 11/29/2014, Bing Li
			while (!this.requestQueue.isEmpty())
			{
				// Dequeue the request from the queue of the dispatcher. 11/29/2014, Bing Li
				// If the request is dequeued before the thread is available, it is possible the dequeued request is lost without being assigned to any threads. 04/20/2018, Bing Li
//					request = this.requestQueue.poll();
				// Since all of the threads created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive threads are available. If so, it is possible to assign tasks to them if they are not so busy. 11/29/2014, Bing Li
				while (this.threads.size() > 0)
				{
					// Select the thread whose load is the least and keep the key of the thread. 11/29/2014, Bing Li
					selectedThreadKey = CollectionSorter.minValueKey(this.threads);
					// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/19/2014, Bing Li
					if (selectedThreadKey != null)
					{
						// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/19/2014, Bing Li
						if (this.threads.containsKey(selectedThreadKey))
						{
							try
							{
								// Check whether the thread's load reaches the maximum value. 11/29/2014, Bing Li
								if (this.threads.get(selectedThreadKey).getFunction().isFull())
								{
									// Check if the pool is full. If the least load thread is full as checked by the above condition, it denotes that all of the current alive threads are full. So it is required to create a thread to respond the newly received requests if the thread count of the pool does not reach the maximum. 11/29/2014, Bing Li
//										if (!this.createThread(request, this.getMaxThreadSize()))
									if (!this.createThread(this.getPoolSize()))
									{
										// Force to put the request into the queue when the count of threads reaches the upper limit and each of the thread's queue is full. 11/29/2014, Bing Li
										this.threads.get(selectedThreadKey).getFunction().enqueue(this.requestQueue.poll());
									}
								}
								else
								{
									// If the least load thread's queue is not full, just put the request into the queue. 11/29/2014, Bing Li
									this.threads.get(selectedThreadKey).getFunction().enqueue(this.requestQueue.poll());
								}
								// Jump out from the loop since the request is put into a thread. 11/29/2014, Bing Li
								break;
							}
							catch (NullPointerException e)
							{
								// Since no concurrency is applied here, it is possible that a NullPointerException is raised. If so, it means that the selected thread is not available. Just continue to select anther one. 11/29/2014, Bing Li
								continue;
							}
						}
					}
				}
				// If no threads are available, it needs to create a new one to take the request. 11/29/2014, Bing Li
//					this.createThread(request);
				this.createThread();
				// If the dispatcher is shutdown, it is not necessary to keep processing the requests. So, jump out the loop and the thread is dead. 11/29/2014, Bing Li
				if (this.isShutdown())
				{
					break;
				}
			}
			// Check whether the dispatcher is shutdown or not. 11/29/2014, Bing Li
			if (!this.isShutdown())
			{
				// If the dispatcher is still alive, it denotes that no requests are available temporarily. Just wait for a while. 11/29/2014, Bing Li
				this.holdOn();
				// Check whether the request queue is empty. 01/13/2016, Bing Li
				if (this.requestQueue.size() <= 0)
				{
					// Check whether the count of the loops exceeds the predefined value. 01/13/2016, Bing Li
//					if (currentRound.getAndIncrement() >= this.getWaitRound())
//					{
						// Check whether the threads are all disposed. 01/13/2016, Bing Li
//						if (this.threads.isEmpty())
//						{
							/*
							 * 
							 * The run() method is critical. It should NOT be shutdown by the dispatcher itself. It can only be shutdown by outside managers. Otherwise, new messages might NOT be processed because no new threads are created for the run() is returned and the dispatcher is dead. 11/07/2021, Bing Li
							 * 
							 */
							// Dispose the dispatcher. 01/13/2016, Bing Li
//								this.dispose();
//								break;
//						}
//					}
				}
			}
		}
	}
}
