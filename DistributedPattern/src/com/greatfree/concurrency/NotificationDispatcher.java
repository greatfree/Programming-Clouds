package com.greatfree.concurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.util.CollectionSorter;
import com.greatfree.util.UtilConfig;

/*
 * This is an important class that enqueues notifications and creates threads to process them concurrently. It works in the way like a dispatcher. That is why it is named. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class NotificationDispatcher<Notification extends ServerMessage, NotificationThread extends NotificationQueue<Notification>, ThreadCreator extends NotificationThreadCreatable<Notification, NotificationThread>> extends Thread implements CheckIdleable
{
	// Declare a map to contain all of the threads. 11/04/2014, Bing Li
	private Map<String, NotificationThread> threadMap;
	// Declare a queue to contain notifications. Here the queue is specified when declaring. It aims to use the method take() instead of poll() since take() waits when no data in the queue while poll() returns null. The effect of take() is usually preferred. 11/04/2014, Bing Li 
	private LinkedBlockingQueue<Notification> notificationQueue;
	// Declare a thread pool that is used to run a thread. 11/04/2014, Bing Li
	private ThreadPool threadPool;
	// Declare a thread creator that is used to initialize a thread instance. 11/04/2014, Bing Li
	private ThreadCreator threadCreator;
	// Declare the maximum task length for each thread to be created. 11/04/2014, Bing Li
	private int maxTaskSize;
	// Declare the maximum thread count that can be created in the dispatcher. 11/04/2014, Bing Li
	private int maxThreadSize;
	// Declare a timer that controls the task of idle checking. 11/04/2014, Bing Li
	private Timer checkTimer;
	// Declare the checker to check whether created threads are idle long enough. 11/04/2014, Bing Li
	private ThreadIdleChecker<NotificationDispatcher<Notification, NotificationThread, ThreadCreator>> idleChecker;
	// The collaborator is used to pause the dispatcher when no notifications are available and notify to continue when new notifications are received. 11/04/2014, Bing Li
	private Collaborator workCollaborator;
	// The time to wait when no notifications are available. 11/04/2014, Bing Li
	private long dispatcherWaitTime;
	// A flag that indicates whether a local thread pool is used or a global one is used. Since it might get problems if the global pool is shutdown inside the class. Thus, it is required to set the flag. 11/19/2014, Bing Li
	private boolean isSelfThreadPool;

	/*
	 * When a server dispatcher has a local thread pool for each message dispatcher, the constructor is used. 11/04/2014, Bing Li
	 */
	public NotificationDispatcher(int poolSize, long keepAliveTime, ThreadCreator threadCreator, int maxTaskSize, int maxThreadSize, long dispatcherWaitTime)
	{
		this.threadMap = new ConcurrentHashMap<String, NotificationThread>();
		this.notificationQueue = new LinkedBlockingQueue<Notification>();
		this.threadPool = new ThreadPool(poolSize, keepAliveTime);
		this.threadCreator = threadCreator;
		this.maxTaskSize = maxTaskSize;
		this.maxThreadSize = maxThreadSize;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.workCollaborator = new Collaborator();
		this.dispatcherWaitTime = dispatcherWaitTime;
		this.isSelfThreadPool = true;
	}
	
	/*
	 * When a server dispatcher has a global thread pool, the constructor is used. 11/04/2014, Bing Li
	 */
	public NotificationDispatcher(ThreadPool threadPool, ThreadCreator threadCreator, int maxTaskSize, int maxThreadSize, long dispatcherWaitTime)
	{
		this.threadMap = new ConcurrentHashMap<String, NotificationThread>();
		this.notificationQueue = new LinkedBlockingQueue<Notification>();
		this.threadPool = threadPool;
		this.threadCreator = threadCreator;
		this.maxTaskSize = maxTaskSize;
		this.maxThreadSize = maxThreadSize;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.workCollaborator = new Collaborator();
		this.dispatcherWaitTime = dispatcherWaitTime;
		this.isSelfThreadPool = true;
	}
	
	/*
	 * Dispose the notification dispatcher. 11/04/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		// Set the shutdown flag to be true. Thus, the loop in the dispatcher thread to schedule notification loads is terminated. 11/04/2014, Bing Li
		this.workCollaborator.setShutdown();
		// Notify the dispatcher thread that is waiting for the notifications to terminate the waiting. 11/04/2014, Bing Li 
		this.workCollaborator.signalAll();
		// Clear the notification queue. 11/04/2014, Bing Li
		if (this.notificationQueue != null)
		{
			this.notificationQueue.clear();
		}
		// Cancel the timer that controls the idle checking. 11/04/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		// Terminate the periodically running thread for idle checking. 11/04/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Dispose all of threads created during the dispatcher's running procedure. 11/04/2014, Bing Li
		for (NotificationThread thread : this.threadMap.values())
		{
			thread.dispose();
		}
		// Clear the threads. 11/04/2014, Bing Li
		this.threadMap.clear();
		// Check whether the thread pool is local or not. 11/19/2014, Bing Li
		if (this.isSelfThreadPool)
		{
			// Shutdown the thread pool if it belongs to the instance of the class only. 11/04/2014, Bing Li
			this.threadPool.shutdown();
		}
		// Dispose the thread creator. 11/04/2014, Bing Li
		this.threadCreator = null;
	}

	/*
	 * Set the idle checking parameters. 11/04/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// Initialize the timer. 11/04/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the idle checker. 11/04/2014, Bing Li
		this.idleChecker = new ThreadIdleChecker<NotificationDispatcher<Notification, NotificationThread, ThreadCreator>>(this);
		// Schedule the idle checking task. 11/04/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of threads within the dispatcher. 11/04/2014, Bing Li
	 */
	@Override
	public void checkIdle()
	{
		// Check each thread managed by the dispatcher. 11/04/2014, Bing Li
		for (NotificationThread thread : this.threadMap.values())
		{
			// If the thread is empty and idle, it is the one to be checked. 11/04/2014, Bing Li
			if (thread.isEmpty() && thread.isIdle())
			{
				// The algorithm to determine whether a thread should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/04/2014, Bing Li
				this.threadMap.remove(thread.getKey());
				// Dispose the thread. 11/04/2014, Bing Li
				thread.dispose();
				// Collect the resource of the thread. 11/04/2014, Bing Li
				thread = null;
			}
		}
	}

	/*
	 * Enqueue the newly received notification into the dispatcher. 11/04/2014, Bing Li
	 */
	public synchronized void enqueue(Notification notification)
	{
		// Enqueue the notification into the queue. 11/04/2014, Bing Li
		this.notificationQueue.add(notification);
		// Notify the dispatcher thread, which is possibly blocked when no requests are available, to keep working. 11/04/2014, Bing Li
		this.workCollaborator.signal();
	}
	
	/*
	 * The thread of the dispatcher is always running until no notifications from remote nodes will be received. If too many notifications are received, more threads are created by the dispatcher. If notifications are limited, the count of threads created by the dispatcher is also small. It is possible no any threads are alive when no notifications are received for a long time. 11/04/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification. 11/04/2014, Bing Li
		Notification notification;
		// Initialize a task map to calculate the load of each thread. 11/05/2014, Bing Li
		Map<String, Integer> threadTaskMap = new HashMap<String, Integer>();
		// Declare a string to keep the selected thread key. 11/04/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// The dispatcher usually runs all of the time unless the server is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/05/2014, Bing Li
		while (!this.workCollaborator.isShutdown())
		{
			try
			{
				// Check whether notifications are received and saved in the queue. 11/05/2014, Bing Li
				while (!this.notificationQueue.isEmpty())
				{
					// Dequeue the notification from the queue of the dispatcher. 11/05/2014, Bing Li
					notification = this.notificationQueue.take();
				
					// Since all of the threads created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive threads are available. If so, it is possible to assign tasks to them if they are not so busy. 11/05/2014, Bing Li
					while (this.threadMap.size() > 0)
					{
						// Clear the map to start to calculate the loads of those threads. 11/05/2014, Bing Li
						threadTaskMap.clear();
	
						// Each thread's workload is saved into the threadTaskMap. 11/05/2014, Bing Li
						for (NotificationThread thread : this.threadMap.values())
						{
							threadTaskMap.put(thread.getKey(), thread.getQueueSize());
						}
						// Select the thread whose load is the least and keep the key of the thread. 11/05/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(threadTaskMap);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/19/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/19/2014, Bing Li
							if (this.threadMap.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the thread's load reaches the maximum value. 11/05/2014, Bing Li
									if (this.threadMap.get(selectedThreadKey).isFull())
									{
										// Check if the pool is full. If the least load thread is full as checked by the above condition, it denotes that all of the current alive threads are full. So it is required to create a thread to respond the newly received notifications if the thread count of the pool does not reach the maximum. 11/05/2014, Bing Li
										if (this.threadMap.size() < this.maxThreadSize)
										{
											// Create a new thread. 11/05/2014, Bing Li
											NotificationThread thread = this.threadCreator.createNotificationThreadInstance(this.maxTaskSize);
											// Save the newly created thread into the map. 11/05/2014, Bing Li
											this.threadMap.put(thread.getKey(), thread);
											// Enqueue the notification into the queue of the newly created thread. Then, the notification will be processed by the thread. 11/05/2014, Bing Li
											this.threadMap.get(thread.getKey()).enqueue(notification);
											// Start the thread by the thread pool. 11/05/2014, Bing Li
											this.threadPool.execute(this.threadMap.get(thread.getKey()));
										}
										else
										{
											// Force to put the notification into the queue when the count of threads reaches the upper limit and each of the thread's queue is full. 11/05/2014, Bing Li
											this.threadMap.get(selectedThreadKey).enqueue(notification);
										}
									}
									else
									{
										// If the least load thread's queue is not full, just put the notification into the queue. 11/05/2014, Bing Li
										this.threadMap.get(selectedThreadKey).enqueue(notification);
									}
									
									// Jump out from the loop since the notification is put into a thread. 11/05/2014, Bing Li
									break;
								}
								catch (NullPointerException e)
								{
									// Since no concurrency is applied here, it is possible that a NullPointerException is raised. If so, it means that the selected thread is not available. Just continue to select anther one. 11/19/2014, Bing Li
									continue;
								}
							}
						}
					}
					// If no threads are available, it needs to create a new one to take the notification. 11/05/2014, Bing Li
					if (this.threadMap.size() <= 0)
					{
						// Create a new thread. 11/05/2014, Bing Li
						NotificationThread thread = this.threadCreator.createNotificationThreadInstance(this.maxTaskSize);
						// Put it into the map for further reuse. 11/05/2014, Bing Li
						this.threadMap.put(thread.getKey(), thread);
						// Take the notification. 11/05/2014, Bing Li
						this.threadMap.get(thread.getKey()).enqueue(notification);
						// Start the thread. 11/05/2014, Bing Li
						this.threadPool.execute(this.threadMap.get(thread.getKey()));
					}
	
					// If the dispatcher is shutdown, it is not necessary to keep processing the notifications. So, jump out the loop and the thread is dead. 11/05/2014, Bing Li
					if (this.workCollaborator.isShutdown())
					{
						break;
					}
				}
	
				// Check whether the dispatcher is shutdown or not. 11/05/2014, Bing Li
				if (!this.workCollaborator.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no notifications are available temporarily. Just wait for a while. 11/05/2014, Bing Li
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
