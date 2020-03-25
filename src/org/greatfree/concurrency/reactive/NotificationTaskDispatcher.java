package org.greatfree.concurrency.reactive;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.concurrency.ConcurrentDispatcher;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.ThreadIdleChecker;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.ServerStatus;

// Created: 01/21/2019, Bing Li
public class NotificationTaskDispatcher<Notification, NotificationThread extends NotificationTaskQueue<Notification>, ThreadCreator extends NotificationTaskThreadCreator<Notification, NotificationThread>> extends ConcurrentDispatcher
{
	// Declare a map to contain all of the threads. 11/04/2014, Bing Li
	private Map<String, Runner<NotificationThread>> threads;
	// Declare a queue to contain objects as notifications. Here the queue is specified when declaring. It aims to use the method take() instead of poll() since take() waits when no data in the queue while poll() returns null. The effect of take() is usually preferred. 11/04/2014, Bing Li 
	private Queue<Notification> notificationQueue;
	// Declare a thread creator that is used to initialize a thread instance. 11/04/2014, Bing Li
	private ThreadCreator threadCreator;
	// Declare the checker to check whether created threads are idle long enough. 11/04/2014, Bing Li
	private ThreadIdleChecker<NotificationTaskDispatcher<Notification, NotificationThread, ThreadCreator>> idleChecker;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	private NotificationTask<Notification> task;

	public NotificationTaskDispatcher(NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> builder)
	{
		super(builder.getPoolSize(), builder.getMaxTaskSize(), builder.getScheduler(), builder.getDispatcherWaitTime(), builder.getIdleCheckDelay(), builder.getIdleCheckPeriod(), builder.getWaitRound());
		this.threads = new ConcurrentHashMap<String, Runner<NotificationThread>>();
		this.notificationQueue = new LinkedBlockingQueue<Notification>();
		this.threadCreator = builder.getCreator();
		this.idleChecker = new ThreadIdleChecker<NotificationTaskDispatcher<Notification, NotificationThread, ThreadCreator>>(this);
		this.task = builder.getTask();
	}

	public static class NotificationTaskDispatcherBuilder<Notification, NotificationThread extends NotificationTaskQueue<Notification>, ThreadCreator extends NotificationTaskThreadCreator<Notification, NotificationThread>> implements Builder<NotificationTaskDispatcher<Notification, NotificationThread, ThreadCreator>>
	{
		private int poolSize;
		private ThreadCreator threadCreator;
		private int maxTaskSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		private NotificationTask<Notification> task;
		
		public NotificationTaskDispatcherBuilder()
		{
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> threadCreator(ThreadCreator threadCreator)
		{
			this.threadCreator = threadCreator;
			return this;
		}
		
		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> notificationQueueSize(int maxTaskSize)
		{
			this.maxTaskSize = maxTaskSize;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		public NotificationTaskDispatcherBuilder<Notification, NotificationThread, ThreadCreator> task(NotificationTask<Notification> task)
		{
			this.task = task;
			return this;
		}

		@Override
		public NotificationTaskDispatcher<Notification, NotificationThread, ThreadCreator> build()
		{
			return new NotificationTaskDispatcher<Notification, NotificationThread, ThreadCreator>(this);
		}

		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		public ThreadCreator getCreator()
		{
			return this.threadCreator;
		}
		
		public int getMaxTaskSize()
		{
			return this.maxTaskSize;
		}
		
		public long getDispatcherWaitTime()
		{
			return this.dispatcherWaitTime;
		}
		
		public int getWaitRound()
		{
			return this.waitRound;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		
		public NotificationTask<Notification> getTask()
		{
			return this.task;
		}
	}

	/*
	 * Since the dispatcher is killed when no tasks are available for sufficient time, it needs to be restarted when new tasks need to be processed. The method is used for that. 01/14/2016, Bing Li
	 */
	private void restart()
	{
		// Reset the collaborator for the new resumption. 02/01/2016, Bing Li
		this.resetSync();
		// If the map of threads is disposed, it needs to initialize a new one. 01/14/2016, Bing Li
		if (this.threads == null)
		{
			// Create a new map to keep the threads. 01/14/2016, Bing Li
			this.threads = new ConcurrentHashMap<String, Runner<NotificationThread>>();
		}
		// If the queue of requests is disposed, it needs to initialize a new one. 01/14/2016, Bing Li
		if (this.notificationQueue == null)
		{
			// Create a new queue to keep the requests. 01/14/2016, Bing Li
			this.notificationQueue = new LinkedBlockingQueue<Notification>();
		}
		// Reset the idle checking. 01/14/2016, Bing Li
		this.setIdleChecker(super.getIdleCheckDelay(), super.getIdleCheckPeriod());
	}

	@Override
	public void dispose() throws InterruptedException
	{
		// Set the sync as shutdown. 02/26/2016, Bing Li
		super.shutdownSync();

		// Clear the notification queue. 11/04/2014, Bing Li
		if (this.notificationQueue != null)
		{
			this.notificationQueue.clear();
		}
		// Detect whether the idle-checking task is initialized. 02/01/2016, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Cancel the idle-checking task. 02/01/2016, Bing Li
			this.idleCheckingTask.cancel(true);
		}
		// Dispose all of threads created during the dispatcher's running procedure. 11/04/2014, Bing Li
		for (Runner<NotificationThread> thread : this.threads.values())
		{
			thread.stop();
		}
		// Clear the threads. 11/04/2014, Bing Li
		this.threads.clear();
	}

	@Override
	public boolean isReady()
	{
		// Check whether the dispatcher is shut down. 01/14/2016, Bing Li
		if (!super.isShutdown())
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

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		try
		{
			// Schedule the idle-checking task and obtain the task instance, which is used to cancel the task when required. 02/01/2016, Bing Li
			this.idleCheckingTask = super.getScheduler().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
		}
		catch (RejectedExecutionException e)
		{
			// When the scheduling is rejected for shutting down the server, the exception is not necessary to be shown. 02/01/2016, Bing Li
			ServerStatus.FREE().printException(e);
		}
	}

	@Override
	public void checkIdle() throws InterruptedException
	{
		// Check each thread managed by the dispatcher. 11/04/2014, Bing Li
		for (Runner<NotificationThread> thread : this.threads.values())
		{
			// If the thread is empty and idle, it is the one to be checked. 11/04/2014, Bing Li
			if (thread.getFunction().isIdle())
			{
				// The algorithm to determine whether a thread should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/04/2014, Bing Li
				this.threads.remove(thread.getFunction().getKey());
				// Dispose the thread. 11/04/2014, Bing Li
				thread.stop();
				// Collect the resource of the thread. 11/04/2014, Bing Li
				thread = null;
			}
		}
	}

	/*
	 * Enqueue the newly received notification into the dispatcher. 11/04/2014, Bing Li
	 */
	public void enqueue(Notification notification)
	{
		// Enqueue the object as a notification into the queue. 11/04/2014, Bing Li
		this.notificationQueue.add(notification);
		// Notify the dispatcher thread, which is possibly blocked when no requests are available, to keep working. 11/04/2014, Bing Li
		super.signal();
	}

	/*
	 * Check whether the notification queue is empty or not. 02/07/2016, Bing Li
	 */
	public boolean isEmpty()
	{
		return this.notificationQueue.isEmpty();
	}

	/*
	 * Check whether the dispatcher is busy or not. 02/07/2016, Bing Li
	 */
	public boolean isBusy()
	{
		// Check each thread managed by the dispatcher. 02/07/2016, Bing Li
		for (Runner<NotificationThread> thread : this.threads.values())
		{
			// Check whether one thread is idle or busy. 02/07/2016, Bing Li
			if (!thread.getFunction().isIdle())
			{
				// If one thread is busy, it represents the dispatcher is busy. 02/07/2016, Bing Li
				return true;
			}
		}
		// If all of the threads are idle, it represnts the dispatcher is idle. 02/07/2016, Bing Li
		return false;
	}

	/*
	 * It is required to synchronize the process to create threads. It is noted that a newly created thread is not put into the map when the dispatcher is disposed. If so, the thread cannot be shutdown even when the process is shutdown. Any threads must be put into the map for management. 08/10/2016, Bing Li
	 */
//	private synchronized boolean createThread(Notification notification, int upperSize)
	private boolean createThread(int upperSize)
	{
		// Check whether the thread pool reaches the maximum size. 12/03/2016, Bing Li
		if (this.threads.size() < upperSize)
		{
			// Create a new thread. 11/29/2014, Bing Li
			NotificationThread thread = this.threadCreator.createThreadInstance(this.getMaxTaskSizePerThread(), this.task);
			// Take the notification. 11/29/2014, Bing Li
			thread.enqueue(this.notificationQueue.poll());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<NotificationThread> runner = new Runner<NotificationThread>(thread);
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
//	private synchronized boolean createThread(Notification notification)
	private boolean createThread()
	{
		// Check whether the thread pool is empty. 12/03/2016, Bing Li
		if (this.threads.size() <= 0)
		{
			// Create a new thread. 11/29/2014, Bing Li
			NotificationThread thread = this.threadCreator.createThreadInstance(this.getMaxTaskSizePerThread(), this.task);
			// Take the notification. 11/29/2014, Bing Li
			thread.enqueue(this.notificationQueue.poll());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<NotificationThread> runner = new Runner<NotificationThread>(thread);
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

	@Override
	public void run()
	{
		String selectedThreadKey;
		// The value is used to count the count of loops for the dispatcher when no tasks are available. 01/13/2016, Bing Li
		AtomicInteger currentRound = new AtomicInteger(0);
		// The dispatcher usually runs all of the time unless the server is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/05/2014, Bing Li
		while (!this.isShutdown())
		{
			try
			{
				// Check whether notifications are received and saved in the queue. 11/05/2014, Bing Li
				while (!this.notificationQueue.isEmpty())
				{
					// Dequeue one object from the queue of the dispatcher. 11/05/2014, Bing Li
					// If the notification is dequeued before the thread is available, it is possible the dequeued notification is lost without being assigned to any threads. 04/20/2018, Bing Li
//					notification = this.notificationQueue.poll();
					// Since all of the threads created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive threads are available. If so, it is possible to assign tasks to them if they are not so busy. 11/05/2014, Bing Li
					while (this.threads.size() > 0)
					{
						// Select the thread whose load is the least and keep the key of the thread. 11/05/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(this.threads);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/19/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/19/2014, Bing Li
							if (this.threads.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the thread's load reaches the maximum value. 11/05/2014, Bing Li
									if (this.threads.get(selectedThreadKey).getFunction().isFull())
									{
										// Check if the pool is full. If the least load thread is full as checked by the above condition, it denotes that all of the current alive threads are full. So it is required to create a thread to respond the newly received notifications if the thread count of the pool does not reach the maximum. 11/05/2014, Bing Li
//										if (!this.createThread(notification, this.getMaxThreadSize()))
										if (!this.createThread(this.getPoolSize()))
										{
											// Since the thread might be disposed when the notification is enqueued, it needs to check. 02/22/2016, Bing Li
											// Force to put the notification into the queue when the count of threads reaches the upper limit and each of the thread's queue is full. 11/05/2014, Bing Li
											this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
										}
									}
									else
									{
										this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
									}
									break;
								}
								catch (NullPointerException e)
								{
									// Since no concurrency control for threadMap, it is possible that the selected thread is removed for being idle. If so, just select another one. 03/20/2014, Bing Li
									continue;
								}
							}
						}
					}
					// If no threads are available, it needs to create a new one to take the notification. 11/05/2014, Bing Li
//					this.createThread(notification);
					this.createThread();
					// If the dispatcher is shutdown, it is not necessary to keep processing the notifications. So, jump out the loop and the thread is dead. 11/05/2014, Bing Li
					if (super.isShutdown())
					{
						break;
					}
				}
				// Check whether the dispatcher is shutdown or not. 11/05/2014, Bing Li
				if (!super.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no notifications are available temporarily. Just wait for a while. 11/05/2014, Bing Li
					super.holdOn();
					if (this.notificationQueue.size() <= 0)
					{
						// Check whether the count of the loops exceeds the predefined value. 01/14/2016, Bing Li
						if (currentRound.getAndIncrement() >= this.getWaitRound())
						{
							// Check whether the threads are all disposed. 01/14/2016, Bing Li
							if (this.threads.isEmpty())
							{
								// Dispose the dispatcher. 01/14/2016, Bing Li
								this.dispose();
								break;
							}
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
