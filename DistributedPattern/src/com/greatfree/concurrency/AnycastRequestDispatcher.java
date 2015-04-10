package com.greatfree.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.multicast.AnycastRequest;
import com.greatfree.multicast.AnycastResponse;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.util.CollectionSorter;
import com.greatfree.util.UtilConfig;

/*
 * This is an important class that enqueues requests and creates anycast queue threads to respond them concurrently. If the local data is not available, it is necessary to forward the request to the local node's children. 11/29/2014, Bing Li
 * 
 * It works in the way like a scheduler or a dispatcher. That is why it is named. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class AnycastRequestDispatcher<Request extends AnycastRequest, Response extends AnycastResponse, RequestThread extends AnycastRequestQueue<Request, Response>, RequestThreadCreator extends AnycastRequestThreadCreatable<Request, Response, RequestThread>> extends Thread implements CheckIdleable
{
	// Declare a map to contain all of the threads. 11/29/2014, Bing Li
	private Map<String, RequestThread> threadMap;
	// Declare a queue to contain requests. 11/29/2014, Bing Li 
	private LinkedBlockingQueue<Request> requestQueue;
	// Declare a thread pool that is used to run a thread. 11/29/2014, Bing Li
	private FreeClientPool pool;
	// The IP/port is the initiator of the anycast requesting. Keep the information to send the response to it once if the result is obtained. 11/29/2014, Bing Li
	private IPPort serverAddress;
	// Declare a thread pool that is used to run a thread. 11/29/2014, Bing Li
	private ThreadPool threadPool;
	// Declare a thread creator that is used to initialize a thread instance. 11/29/2014, Bing Li
	private RequestThreadCreator threadCreator;
	// Declare the maximum task length for each thread to be created. 11/29/2014, Bing Li
	private int maxTaskSize;
	// Declare the maximum thread count that can be created in the dispatcher. 11/29/2014, Bing Li
	private int maxThreadSize;
	// Declare a timer that controls the task of idle checking. 11/29/2014, Bing Li
	private Timer checkTimer;
	// Declare the checker to check whether created threads are idle long enough. 11/29/2014, Bing Li
	private ThreadIdleChecker<AnycastRequestDispatcher<Request, Response, RequestThread, RequestThreadCreator>> idleChecker;
	// The collaborator is used to pause the dispatcher when no requests are available and notify to continue when new requests are received. 11/29/2014, Bing Li
	private Collaborator workCollaborator;
	// The time to wait when no requests are available. 11/29/2014, Bing Li
	private long dispatcherWaitTime;
	// The flag indicates whether the dispatcher has its own thread pool or shares with others. 11/29/2014, Bing Li
	private boolean isSelfThreadPool;

	/*
	 * Initialize the anycast request dispatcher. The constructor is called when the thread pool is owned by the dispatcher only. 11/29/2014, Bing Li
	 */
	public AnycastRequestDispatcher(FreeClientPool pool, String serverAddress, int serverPort, int poolSize, long keepAliveTime, RequestThreadCreator threadCreator, int maxTaskSize, int maxThreadSize, long dispatcherKeepAliveTime)
	{
		this.pool = pool;
		this.serverAddress = new IPPort(serverAddress, serverPort);
		this.threadMap = new ConcurrentHashMap<String, RequestThread>();
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.threadPool = new ThreadPool(poolSize, keepAliveTime);
		this.threadCreator = threadCreator;
		this.maxTaskSize = maxTaskSize;
		this.maxThreadSize = maxThreadSize;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.workCollaborator = new Collaborator();
		this.dispatcherWaitTime = dispatcherKeepAliveTime;
		this.isSelfThreadPool = true;
	}

	/*
	 * Dispose the anycast request dispatcher. The constructor is called when the thread pool is shared. 11/29/2014, Bing Li
	 */
	public AnycastRequestDispatcher(FreeClientPool pool, String serverAddress, int serverPort, ThreadPool threadPool, RequestThreadCreator threadCreator, int maxTaskSize, int maxThreadSize, long dispatcherKeepAliveTime)
	{
		this.pool = pool;
		this.serverAddress = new IPPort(serverAddress, serverPort);
		this.threadMap = new ConcurrentHashMap<String, RequestThread>();
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.threadPool = threadPool;
		this.threadCreator = threadCreator;
		this.maxTaskSize = maxTaskSize;
		this.maxThreadSize = maxThreadSize;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.workCollaborator = new Collaborator();
		this.dispatcherWaitTime = dispatcherKeepAliveTime;
		this.isSelfThreadPool = false;
	}

	/*
	 * Dispose the request dispatcher. 11/29/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		// Set the shutdown flag to be true. Thus, the loop in the dispatcher thread to schedule requests load is terminated. 11/29/2014, Bing Li
		this.workCollaborator.setShutdown();
		// Notify the dispatcher thread that is waiting for the requests to terminate the waiting. 11/29/2014, Bing Li 
		this.workCollaborator.signalAll();
		// Clear the request queue. 11/29/2014, Bing Li
		if (this.requestQueue != null)
		{
			this.requestQueue.clear();
		}
		// Cancel the timer that controls the idle checking. 11/29/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		// Terminate the periodically running thread for idle checking. 11/29/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Dispose all of threads created during the dispatcher's running procedure. 11/29/2014, Bing Li
		for (RequestThread thread : this.threadMap.values())
		{
			thread.dispose();
		}
		// Clear the threads. 11/29/2014, Bing Li
		this.threadMap.clear();
		// Check whether the thread is owned by the dispatcher only. 11/29/2014, Bing Li
		if (this.isSelfThreadPool)
		{
			// Shutdown the thread pool. 11/29/2014, Bing Li
			this.threadPool.shutdown();
		}
		// Dispose the thread creator. 11/29/2014, Bing Li
		this.threadCreator = null;
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of threads within the dispatcher. 11/29/2014, Bing Li
	 */
	@Override
	public void checkIdle()
	{
		// Check each thread managed by the dispatcher. 11/29/2014, Bing Li
		for (RequestThread thread : this.threadMap.values())
		{
			// If the thread is empty and idle, it is the one to be checked. 11/29/2014, Bing Li
			if (thread.isEmpty() && thread.isIdle())
			{
				// The algorithm to determine whether a thread should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/29/2014, Bing Li
				this.threadMap.remove(thread.getKey());
				// Dispose the thread. 11/29/2014, Bing Li
				thread.dispose();
				// Collect the resource of the thread. 11/29/2014, Bing Li
				thread = null;
			}
		}
	}

	/*
	 * Set the idle checking parameters. 11/29/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// Initialize the timer. 11/29/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the idle checker. 11/29/2014, Bing Li
		this.idleChecker = new ThreadIdleChecker<AnycastRequestDispatcher<Request, Response, RequestThread, RequestThreadCreator>>(this);
		// Schedule the idle checking task. 11/29/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
	}

	/*
	 * Enqueue the newly received request into the dispatcher. 11/29/2014, Bing Li
	 */
	public synchronized void enqueue(Request request)
	{
		// Enqueue the request into the queue. 11/29/2014, Bing Li
		this.requestQueue.add(request);
		// Notify the dispatcher thread, which is possibly blocked when no requests are available, to keep working. 11/29/2014, Bing Li
		this.workCollaborator.signal();
	}
	
	/*
	 * The thread of the dispatcher is always running until no requests from clients will be received. If too many requests are received, more threads are created by the dispatcher to respond users in time. If requests are limited, the count of threads created by the dispatcher is also small. It is possible no any threads are alive when no requests are received for a long time. 11/29/2014, Bing Li
	 */
	public void run()
	{
		// Declare a request. 11/29/2014, Bing Li
		Request request;
		// Initialize a thread map to calculate the load of each thread. 11/29/2014, Bing Li
		Map<String, Integer> threadTaskMap = new HashMap<String, Integer>();
		// Declare a string to keep the selected thread key. 11/29/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// The dispatcher usually runs all of the time unless the server is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/29/2014, Bing Li
		while (!this.workCollaborator.isShutdown())
		{
			try
			{
				// Check whether requests are received and saved in the queue. 11/29/2014, Bing Li
				while (!this.requestQueue.isEmpty())
				{
					// Dequeue the request from the queue of the dispatcher. 11/29/2014, Bing Li
					request = this.requestQueue.take();
					// Since all of the threads created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive threads are available. If so, it is possible to assign tasks to them if they are not so busy. 11/29/2014, Bing Li
					while (this.threadMap.size() > 0)
					{
						// Clear the map to start to calculate the load those threads. 11/29/2014, Bing Li
						threadTaskMap.clear();

						// Each thread's workload is saved into the threadTaskMap. 11/29/2014, Bing Li
						for (RequestThread thread : this.threadMap.values())
						{
							threadTaskMap.put(thread.getKey(), thread.getQueueSize());
						}
						// Select the thread whose load is the least and keep the key of the thread. 11/29/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(threadTaskMap);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/19/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/19/2014, Bing Li
							if (this.threadMap.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the thread's load reaches the maximum value. 11/29/2014, Bing Li
									if (this.threadMap.get(selectedThreadKey).isFull())
									{
										// Check if the pool is full. If the least load thread is full as checked by the above condition, it denotes that all of the current alive threads are full. So it is required to create a thread to respond the newly received requests if the thread count of the pool does not reach the maximum. 11/29/2014, Bing Li
										if (this.threadMap.size() < this.maxThreadSize)
										{
											// Create a new thread. 11/29/2014, Bing Li
											RequestThread thread = this.threadCreator.createRequestThreadInstance(this.serverAddress, this.pool, this.maxTaskSize);
											// Save the newly created thread into the map. 11/29/2014, Bing Li
											this.threadMap.put(thread.getKey(), thread);
											// Enqueue the request into the queue of the newly created thread. Then, the request will be processed and responded by the thread. 11/29/2014, Bing Li
											this.threadMap.get(thread.getKey()).enqueue(request);
											// Start the thread by the thread pool. 11/29/2014, Bing Li
											this.threadPool.execute(this.threadMap.get(thread.getKey()));
										}
										else
										{
											// Force to put the request into the queue when the count of threads reaches the upper limit and each of the thread's queue is full. 11/29/2014, Bing Li
											this.threadMap.get(selectedThreadKey).enqueue(request);
										}
									}
									else
									{
										// If the least load thread's queue is not full, just put the request into the queue. 11/29/2014, Bing Li
										this.threadMap.get(selectedThreadKey).enqueue(request);
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
					if (this.threadMap.size() <= 0)
					{
						// Create a new thread. 11/29/2014, Bing Li
						RequestThread thread = this.threadCreator.createRequestThreadInstance(this.serverAddress, this.pool, this.maxTaskSize);
						// Put it into the map for further reuse. 11/29/2014, Bing Li
						this.threadMap.put(thread.getKey(), thread);
						// Take the request. 11/29/2014, Bing Li
						this.threadMap.get(thread.getKey()).enqueue(request);
						// Start the thread. 11/29/2014, Bing Li
						this.threadPool.execute(this.threadMap.get(thread.getKey()));
					}
					// If the dispatcher is shutdown, it is not necessary to keep processing the requests. So, jump out the loop and the thread is dead. 11/29/2014, Bing Li
					if (this.workCollaborator.isShutdown())
					{
						break;
					}
				}
				// Check whether the dispatcher is shutdown or not. 11/29/2014, Bing Li
				if (!this.workCollaborator.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no requests are available temporarily. Just wait for a while. 11/29/2014, Bing Li
					this.workCollaborator.holdOn(this.dispatcherWaitTime);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
