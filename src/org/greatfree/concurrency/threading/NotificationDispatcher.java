package org.greatfree.concurrency.threading;

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
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.UtilConfig;

/*
 * Different from the reactive notification dispatcher, the one is not only an independent thread pool to manage messaging-based threads, but also the one to be remotely controlled by programmers to deal with sophisticated concurrent problems that cannot be predefined. 09/10/2019, Bing Li
 */

// Created: 09/10/2019, Bing Li
public class NotificationDispatcher<Notification extends ServerMessage, NotificationThread extends NotificationQueue<Notification>, ThreadCreator extends NotificationThreadCreatable<Notification, NotificationThread>> extends ConcurrentDispatcher
{
	// Declare a map to contain all of the threads. 11/04/2014, Bing Li
	private Map<String, Runner<NotificationThread>> threads;
	// Declare a queue to contain notifications. Here the queue is specified when declaring. It aims to use the method take() instead of poll() since take() waits when no data in the queue while poll() returns null. The effect of take() is usually preferred. 11/04/2014, Bing Li 
	private Queue<Notification> notificationQueue;
	// Declare a thread creator that is used to initialize a thread instance. 11/04/2014, Bing Li
	private ThreadCreator threadCreator;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// Declare the checker to check whether created threads are idle long enough. 11/04/2014, Bing Li
	private ThreadIdleChecker<NotificationDispatcher<Notification, NotificationThread, ThreadCreator>> idleChecker;
	
	public NotificationDispatcher(NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> builder)
	{
		super(builder.getPoolSize(), builder.getNotificationQueueSize(), builder.getScheduler(), builder.getDispatcherWaitTime(), builder.getIdleCheckDelay(), builder.getIdleCheckPeriod(), builder.getWaitRound());
		this.threads = new ConcurrentHashMap<String, Runner<NotificationThread>>();
		this.notificationQueue = new LinkedBlockingQueue<Notification>();
		this.threadCreator = builder.getCreator();
		this.idleChecker = new ThreadIdleChecker<NotificationDispatcher<Notification, NotificationThread, ThreadCreator>>(this);
	}
	
	public static class NotificationDispatcherBuilder<Notification extends ServerMessage, NotificationThread extends NotificationQueue<Notification>, ThreadCreator extends NotificationThreadCreatable<Notification, NotificationThread>> implements Builder<NotificationDispatcher<Notification, NotificationThread, ThreadCreator>>
	{
		private int poolSize;
		private ThreadCreator threadCreator;
		private int notificationQueueSize;
		private long dispatcherWaitTime;
		private int waitRound;
		private long idleCheckDelay;
		private long idleCheckPeriod;
		private ScheduledThreadPoolExecutor scheduler;
		
		public NotificationDispatcherBuilder()
		{
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}
		
		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> threadCreator(ThreadCreator threadCreator)
		{
			this.threadCreator = threadCreator;
			return this;
		}
		
		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> notificationQueueSize(int notificationQueueSize)
		{
			this.notificationQueueSize = notificationQueueSize;
			return this;
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> dispatcherWaitTime(long dispatcherWaitTime)
		{
			this.dispatcherWaitTime = dispatcherWaitTime;
			return this;
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public NotificationDispatcherBuilder<Notification, NotificationThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		@Override
		public NotificationDispatcher<Notification, NotificationThread, ThreadCreator> build()
		{
			return new NotificationDispatcher<Notification, NotificationThread, ThreadCreator>(this);
		}

		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		public ThreadCreator getCreator()
		{
			return this.threadCreator;
		}
		
		public int getNotificationQueueSize()
		{
			return this.notificationQueueSize;
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
	}

	/*
	 * Since the dispatcher is killed when no tasks are available for sufficient time, it needs to be restarted when new tasks need to be processed. The method is used for that. 01/14/2016, Bing Li
	 */
	private void restart()
	{
		// Reset the collaborator for the new resumption. 02/01/2016, Bing Li
		super.resetSync();
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

	/*
	 * Dispose the notification dispatcher. 11/04/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
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
//			if (!thread.getFunction().isFull())
			if (!thread.getFunction().isHung())
			{
//				thread.dispose();
				thread.stop();
			}
			else
			{
				thread.interrupt();
			}
//			thread.interrupt();
		}
		// Clear the threads. 11/04/2014, Bing Li
		this.threads.clear();
	}

	/*
	 * Detect whether the dispatcher is ready for receiving new tasks or not. If not, it needs to restart the dispatcher. 01/14/2016, Bing Li
	 */
	@Override
	protected synchronized boolean isReady()
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
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	public synchronized String generateThread()
	{
		// Check whether the thread pool reaches the maximum size. 12/03/2016, Bing Li
		if (this.threads.size() < super.getPoolSize())
		{
			// Create a new thread. 11/29/2014, Bing Li
			NotificationThread thread = this.threadCreator.createNotificationThreadInstance(this.getMaxTaskSizePerThread());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<NotificationThread> runner = new  Runner<NotificationThread>(thread);
			// Put it into the map for further reuse. 11/29/2014, Bing Li
			this.threads.put(thread.getKey(), runner);
			// Return true if a new thread is created. 12/03/2016, Bing Li
			return thread.getKey();
		}
		// Return false if no new thread is created. 12/03/2016, Bing Li
		return ThreadConfig.NO_THREAD_KEY;
	}
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
//	public void enqueueInstruction(String threadKey, SyncInstructNotification n)
	public synchronized void enqueueInstruction(String threadKey, Notification n)
	{
		if (this.threads.containsKey(threadKey))
		{
			this.threads.get(threadKey).getFunction().enqueue(n);
		}
	}
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	public synchronized void execute(String threadKey)
	{
		if (this.threads.containsKey(threadKey))
		{
//			System.out.println("NotificationDispatcher-execute(): one thread is starting ...");
			this.threads.get(threadKey).start();
//			System.out.println("NotificationDispatcher-execute(): one thread is started ...");
		}
	}
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	/*
	public void timeout(String threadKey, long timeout) throws InterruptedException
	{
		if (this.threads.containsKey(threadKey))
		{
			this.threads.get(threadKey).getFunction().holdOn(timeout);
		}
	}
	*/
	
	/*
	 * 
	 * The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
	 * 
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 * 
	 */
	/*
	public void signal(String threadKey)
	{
		if (this.threads.containsKey(threadKey))
		{
			this.threads.get(threadKey).getFunction().signal();
		}
	}
	*/
	
	/*
	public void holdOn(String threadKey)
	{
		
	}
	*/
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	public synchronized boolean isAlive(String threadKey)
	{
		if (this.threads.containsKey(threadKey))
		{
			return this.threads.get(threadKey).isAlive();
		}
		return false;
	}
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	public synchronized void shutdown(String threadKey, long timeout) throws InterruptedException
	{
		if (this.threads.containsKey(threadKey))
		{
			this.threads.get(threadKey).getFunction().dispose(timeout);
			this.threads.remove(threadKey);
		}
	}
	
	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/14/2019, Bing Li
	 */
	public synchronized void shutdownAll(long timeout) throws InterruptedException
	{
		for (Map.Entry<String, Runner<NotificationThread>> entry : this.threads.entrySet())
		{
			entry.getValue().getFunction().dispose(timeout);
		}
		this.threads.clear();
	}

	/*
	 * The method is designed for manually controlling from programmers rather than the management from the NotificationDispatcher. 09/11/2019, Bing Li
	 */
	public synchronized void shutdown(String threadKey) throws InterruptedException
	{
		if (this.threads.containsKey(threadKey))
		{
			this.threads.get(threadKey).getFunction().dispose();
			this.threads.remove(threadKey);
		}
	}

	/*
	 * Set the idle checking parameters. 11/04/2014, Bing Li
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
	 * The method is called back by the idle checker periodically to monitor the idle states of threads within the dispatcher. 11/04/2014, Bing Li
	 */
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
				if (!thread.getFunction().isHung())
				{
					thread.stop();
				}
				else
				{
					thread.interrupt();
				}
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
		// Notify the dispatcher thread, which is possibly blocked when no notifications are available, to keep working. 11/04/2014, Bing Li
		super.signal();
	}

	/*
	 * It is required to synchronize the process to create threads. It is noted that a newly created thread is not put into the map when the dispatcher is disposed. If so, the thread cannot be shutdown even when the process is shutdown. Any threads must be put into the map for management. 08/10/2016, Bing Li
	 */
	private synchronized boolean createThread(int upperSize)
	{
		// Check whether the thread pool reaches the maximum size. 12/03/2016, Bing Li
		if (this.threads.size() < upperSize)
		{
			// Create a new thread. 11/29/2014, Bing Li
			NotificationThread thread = this.threadCreator.createNotificationThreadInstance(this.getMaxTaskSizePerThread());
			// Take the notification. 11/29/2014, Bing Li
			Notification n = this.notificationQueue.poll();
			if (n != null)
			{
				thread.enqueue(n);
				// Initialize one instance of Runner. 05/19/2018, Bing Li
				Runner<NotificationThread> runner = new  Runner<NotificationThread>(thread);
				// Start the thread. 11/29/2014, Bing Li
				runner.start();
				// Put it into the map for further reuse. 11/29/2014, Bing Li
				this.threads.put(thread.getKey(), runner);
				// Start the thread. 11/29/2014, Bing Li
//				this.execute(thread);
				// Return true if a new thread is created. 12/03/2016, Bing Li
				return true;
			}
		}
		// Return false if no new thread is created. 12/03/2016, Bing Li
		return false;
	}

	/*
	 * It is required to synchronize the process to create threads. It is noted that a newly created thread is not put into the map when the dispatcher is disposed. If so, the thread cannot be shutdown even when the process is shutdown. Any threads must be put into the map for management. 08/10/2016, Bing Li
	 */
	private synchronized boolean createThread()
	{
		// Check whether the thread pool is empty. 12/03/2016, Bing Li
		if (this.threads.size() <= 0)
		{
			// Create a new thread. 11/29/2014, Bing Li
			NotificationThread thread = this.threadCreator.createNotificationThreadInstance(this.getMaxTaskSizePerThread());
			// Take the notification. 11/29/2014, Bing Li
			thread.enqueue(this.notificationQueue.poll());
			// Initialize one instance of Runner. 05/19/2018, Bing Li
			Runner<NotificationThread> runner = new  Runner<NotificationThread>(thread);
			// Start the thread. 11/29/2014, Bing Li
			runner.start();
			// Put it into the map for further reuse. 11/29/2014, Bing Li
			this.threads.put(thread.getKey(), runner);
			// Return true if a new thread is created. 12/03/2016, Bing Li
			return true;
		}
		// Return false if no new thread is created. 12/03/2016, Bing Li
		return false;
	}

	/*
	 * The thread of the dispatcher is always running until no notifications from remote nodes will be received. If too many notifications are received, more threads are created by the dispatcher. If notifications are limited, the count of threads created by the dispatcher is also small. It is possible no any threads are alive when no notifications are received for a long time. 11/04/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare a string to keep the selected thread key. 11/04/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// The value is used to count the count of loops for the dispatcher when no tasks are available. 01/13/2016, Bing Li
		AtomicInteger currentRound = new AtomicInteger(0);

		// The dispatcher usually runs all of the time unless the server is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/05/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether notifications are received and saved in the queue. 11/05/2014, Bing Li
			while (!this.notificationQueue.isEmpty())
			{
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
									if (!this.createThread(super.getPoolSize()))
									{
										// Since the thread might be disposed when the notification is enqueued, it needs to check. 02/22/2016, Bing Li
										// Force to put the notification into the queue when the count of threads reaches the upper limit and each of the thread's queue is full. 11/05/2014, Bing Li
										this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
									}
								}
								else
								{
									// Since the thread might be disposed when the notification is enqueued, it needs to check. 02/22/2016, Bing Li
									// If the least load thread's queue is not full, just put the notification into the queue. 11/05/2014, Bing Li
									this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
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
				if (super.holdOn())
				{
					// Check whether the request queue is empty. 01/14/2016, Bing Li
					if (this.notificationQueue.size() <= 0)
					{
						// Check whether the count of the loops exceeds the predefined value. 01/14/2016, Bing Li
						if (currentRound.getAndIncrement() >= super.getWaitRound())
						{
							// Check whether the threads are all disposed. 01/14/2016, Bing Li
							if (this.threads.isEmpty())
							{
								/*
								 * 
								 * The run() method is critical. It should NOT be shutdown by the dispatcher itself. It can only be shutdown by outside managers. Otherwise, new messages might NOT be processed because no new threads are created for the run() is returned and the dispatcher is dead. 11/07/2021, Bing Li
								 * 
								 */
								// Dispose the dispatcher. 01/14/2016, Bing Li
//									this.dispose();
//									break;
							}
						}
					}
				}
			}
		}
	}
}
