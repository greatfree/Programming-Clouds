package com.greatfree.remote;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.concurrency.CheckIdleable;
import com.greatfree.concurrency.Collaborator;
import com.greatfree.concurrency.ThreadPool;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.util.CollectionSorter;
import com.greatfree.util.UtilConfig;

/*
 * This class aims to send notifications to a remote server asynchronously without waiting for responses. The sending methods are nonblocking. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class AsyncRemoteEventer<Notification extends ServerMessage> extends Thread implements CheckIdleable
{
	// The eventers that are available to sent the notifications concurrently. They are indexed by the keys which are usually generated upon their IP/ports to be sent to. 11/20/2014, Bing Li
	private Map<String, Eventer<Notification>> eventers;
	// The queue which contains the notification to be sent. The notification contains the IP/port to be sent and the message to be sent. 11/20/2014, Bing Li
	private LinkedBlockingQueue<IPNotification<Notification>> notificationQueue;
	// The thread pool that starts and manages the eventer concurrently. It must be shared with others. 11/20/2014, Bing Li
	private ThreadPool threadPool;
	// The size of the event queue for each eventer. 11/20/2014, Bing Li
	private int eventQueueSize;
	// The count of eventers to be managed. 11/20/2014, Bing Li
	private int eventerSize;
	// The timer to manage the idle checker. 11/20/2014, Bing Li
	private Timer checkTimer;
	// The idle checker to monitor whether an eventer is idle long enough. 11/20/2014, Bing Li
	private EventerIdleChecker<AsyncRemoteEventer<Notification>> idleChecker;
	// The collaborator is used to pause the dispatcher when no notifications are available and notify to continue when new notifications are received. 11/20/2014, Bing Li
	private Collaborator collaborator;
	// The time to be waited when no notifications are available in the class. 11/20/2014, Bing Li
	private long eventingWaitTime;
	// The FreeClientPool that is used to initialize eventers. It must be shared with others. 11/20/2014, Bing Li
	private FreeClientPool clientPool;
	// The time to be waited when no notifications are available in each eventer. 11/20/2014, Bing Li
	private long eventerWaitTime;

	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
	public AsyncRemoteEventer(FreeClientPool clientPool, ThreadPool threadPool, int eventQueueSize, int eventerSize, long eventingWaitTime, long eventerWaitTime)
	{
		this.eventers = new ConcurrentHashMap<String, Eventer<Notification>>();
		this.notificationQueue = new LinkedBlockingQueue<IPNotification<Notification>>();
		this.threadPool = threadPool;
		this.eventQueueSize = eventQueueSize;
		this.eventerSize = eventerSize;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.collaborator = new Collaborator();
		this.eventingWaitTime = eventingWaitTime;
		this.clientPool = clientPool;
		this.eventerWaitTime = eventerWaitTime;
	}

	/*
	 * Dispose the eventer dispatcher. 11/20/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		// Set the shutdown flag to be true. Thus, the loop in the dispatcher thread to schedule notification loads is terminated. 11/20/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the dispatcher thread that is waiting for the notifications to terminate the waiting. 11/20/2014, Bing Li 
		this.collaborator.signalAll();
		// Clear the notification queue. 11/20/2014, Bing Li
		if (this.notificationQueue != null)
		{
			this.notificationQueue.clear();
		}
		// Cancel the timer that controls the idle checking. 11/20/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		// Terminate the periodically running thread for idle checking. 11/20/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Dispose all of eventers created during the dispatcher's running procedure. 11/20/2014, Bing Li
		for (Eventer<Notification> eventer : this.eventers.values())
		{
			eventer.dispose();
		}
		// Clear the eventer map. 11/20/2014, Bing Li
		this.eventers.clear();
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of eventers within the dispatcher. 11/20/2014, Bing Li
	 */
	@Override
	public void checkIdle()
	{
		// Check each eventer managed by the dispatcher. 11/20/2014, Bing Li
		for (Eventer<Notification> eventer : this.eventers.values())
		{
			// If the eventer is empty and idle, it is the one to be checked. 11/20/2014, Bing Li
			if (eventer.isEmpty() && eventer.isIdle())
			{
				// The algorithm to determine whether an eventer should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/20/2014, Bing Li
				this.eventers.remove(eventer.getKey());
				// Dispose the eventer. 11/20/2014, Bing Li
				eventer.dispose();
				// Collect the resource of the eventer. 11/20/2014, Bing Li
				eventer = null;
			}
		}
	}

	/*
	 * Set the idle checking parameters. 11/20/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// Initialize the timer. 11/20/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the idle checker. 11/20/2014, Bing Li
		this.idleChecker = new EventerIdleChecker<AsyncRemoteEventer<Notification>>(this);
		// Schedule the idle checking task. 11/20/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
	}

	/*
	 * Send the notification asynchronously to the IP/port. 11/20/2014, Bing Li
	 */
	public synchronized void notify(String ip, int ipPort, Notification notification)
	{
		// Put the notification and the IP/port into the queue by enclosing them into an instance of IPNotification. 11/20/2014, Bing Li
		this.notificationQueue.add(new IPNotification<Notification>(new IPPort(ip, ipPort), notification));
		// Signal the potential waiting thread to schedule eventers to sent the notification just enqueued. 11/20/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Send the notification asynchronously to a remote node by its key. 11/20/2014, Bing Li
	 */
	public synchronized void notify(String clientKey, Notification notification)
	{
		// Put the notification and the IP/port into the queue by enclosing them into an instance of IPNotification. The IP/port is retrieved from the FreeClientPool by the node key. 11/20/2014, Bing Li
		this.notificationQueue.add(new IPNotification<Notification>(this.clientPool.getIPPort(clientKey), notification));
		// Signal the potential waiting thread to schedule eventers to sent the notification just enqueued. 11/20/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * The thread of the dispatcher is always running until no notifications to be sent. If too many notifications are received, more eventers are created by the dispatcher. If notifications are limited, the count of threads created by the dispatcher is also small. It is possible no any threads are alive when no notifications are received for a long time. 11/20/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification. 11/20/2014, Bing Li
		IPNotification<Notification> notification;
		// Initialize a task map to calculate the load of each eventer. 11/20/2014, Bing Li
		Map<String, Integer> taskMap = new HashMap<String, Integer>();
		// Declare a string to keep the selected eventer key. 11/20/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// The dispatcher usually runs all of the time unless the local node is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/20/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			try
			{
				// Check whether notifications are available in the queue. 11/20/2014, Bing Li
				while (!this.notificationQueue.isEmpty())
				{
					// Dequeue the notification from the queue of the dispatcher. 11/20/2014, Bing Li
					notification = this.notificationQueue.take();

					// Since all of the eventers created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive eventers are available. If so, it is possible to assign tasks to them if they are not so busy. 11/20/2014, Bing Li
					while (this.eventers.size() > 0)
					{
						// Clear the map to start to calculate the loads of those eventers. 11/20/2014, Bing Li
						taskMap.clear();

						// Each eventer's workload is saved into the task map. 11/20/2014, Bing Li
						for (Eventer<Notification> thread : this.eventers.values())
						{
							taskMap.put(thread.getKey(), thread.getQueueSize());
						}
						// Select the eventer whose load is the least and keep the key of the eventer. 11/20/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(taskMap);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/20/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/20/2014, Bing Li
							if (this.eventers.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the eventer's load reaches the maximum value. 11/20/2014, Bing Li
									if (this.eventers.get(selectedThreadKey).isFull())
									{
										// Check if the pool is full. If the least load eventer is full as checked by the above condition, it denotes that all of the current alive eventers are full. So it is required to create an eventer to respond the newly received notifications if the eventer count of the pool does not reach the maximum. 11/20/2014, Bing Li
										if (this.eventers.size() < this.eventerSize)
										{
											// Create a new eventer. 11/20/2014, Bing Li
											Eventer<Notification> thread = new Eventer<Notification>(this.eventQueueSize, this.eventerWaitTime, this.clientPool);
											// Save the newly created eventer into the map. 11/20/2014, Bing Li
											this.eventers.put(thread.getKey(), thread);
											// Enqueue the notification into the queue of the newly created eventer. Then, the notification will be processed by the eventer. 11/20/2014, Bing Li
											this.eventers.get(thread.getKey()).enqueue(notification);
											// Start the eventer by the thread pool. 11/20/2014, Bing Li
											this.threadPool.execute(this.eventers.get(thread.getKey()));
										}
										else
										{
											// Force to put the notification into the queue when the count of eventers reaches the upper limit and each of the eventer's queue is full. 11/20/2014, Bing Li
											this.eventers.get(selectedThreadKey).enqueue(notification);
										}
									}
									else
									{
										// If the least load eventer's queue is not full, just put the notification into the queue. 11/20/2014, Bing Li
										this.eventers.get(selectedThreadKey).enqueue(notification);
									}

									// Jump out from the loop since the notification is put into a thread. 11/20/2014, Bing Li
									break;
								}
								catch (NullPointerException e)
								{
									// Since no concurrency is applied here, it is possible that a NullPointerException is raised. If so, it means that the selected eventer is not available. Just continue to select anther one. 11/20/2014, Bing Li
									continue;
								}
							}
						}
					}
					// If no eventers are available, it needs to create a new one to take the notification. 11/20/2014, Bing Li
					if (this.eventers.size() <= 0)
					{
						// Create a new eventer. 11/20/2014, Bing Li
						Eventer<Notification> thread = new Eventer<Notification>(this.eventQueueSize, this.eventerWaitTime, this.clientPool);
						// Put it into the map for further reuse. 11/20/2014, Bing Li
						this.eventers.put(thread.getKey(), thread);
						// Take the notification. 11/20/2014, Bing Li
						this.threadPool.execute(this.eventers.get(thread.getKey()));
						// Start the thread. 11/20/2014, Bing Li
						this.eventers.get(thread.getKey()).enqueue(notification);
					}

					// If the dispatcher is shutdown, it is not necessary to keep processing the notifications. So, jump out the loop and the eventer is dead. 11/20/2014, Bing Li
					if (this.collaborator.isShutdown())
					{
						break;
					}
				}

				// Check whether the dispatcher is shutdown or not. 11/20/2014, Bing Li
				if (!this.collaborator.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no notifications are available temporarily. Just wait for a while. 11/20/2014, Bing Li
					this.collaborator.holdOn(this.eventingWaitTime);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}
}
