package org.greatfree.concurrency;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.client.IdleChecker;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.UtilConfig;

// Created: 09/09/2018, Bing Li
public class NotifierPool<Notification> extends Thread implements IdleCheckable
{
	// The actors that are available to sent the messages concurrently.. 11/20/2014, Bing Li
	private Map<String, Runner<NotificationThread<Notification>>> threads;
	// The queue which contains the message to be processed. 11/20/2014, Bing Li
	private Queue<Notification> notificationQueue;
	// The size of the message queue for each actor. 11/20/2014, Bing Li
	private final int queueSize;
	// The count of actors to be managed. 11/20/2014, Bing Li
	private final int notifierSize;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// The idle checker to monitor whether an actor is idle long enough. 11/20/2014, Bing Li
	private IdleChecker<NotifierPool<Notification>> idleChecker;
	// The collaborator is used to pause the dispatcher when no messages are available and notify to continue when new messages are received. 11/20/2014, Bing Li
	private Sync collaborator;
	// The time to be waited when no messages are available in the class. 11/20/2014, Bing Li
	private final long poolingWaitTime;
	// The time to be waited when no messages are available in each actor. 11/20/2014, Bing Li
	private final long notifierWaitTime;
	// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
	private final long idleCheckDelay;
	// The idle-checking period. 01/20/2016, Bing Li
	private final long idleCheckPeriod;
	// When the actor has no messages to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the actor has killed after no messages are available for sufficient time. 01/20/2016, Bing Li 
//	private final int waitRound;
	// When self-disposing the dispatcher to save resources, it needs to keep synchronization between the message queue and the disposing. The lock is responsible for that. 02/01/2016, Bing Li
	private ReentrantLock monitorLock;
	// The actor which contains the function to be executed asynchronously. 09/10/2018, Bing Li
	private Notifier<Notification> notifier;

	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
	public NotifierPool(NotifierPoolBuilder<Notification> builder)
	{
		this.threads = new ConcurrentHashMap<String, Runner<NotificationThread<Notification>>>();
		this.notificationQueue = new LinkedBlockingQueue<Notification>();
		this.queueSize = builder.getQueueSize();
		this.notifierSize = builder.getNotifierSize();

		Scheduler.PERIOD().init(builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());

		this.idleChecker = new IdleChecker<NotifierPool<Notification>>(this);
		this.collaborator = new Sync(true);
		this.poolingWaitTime = builder.getPoolingWaitTime();
		this.notifierWaitTime = builder.getNotifierWaitTime();
//		this.waitRound = builder.getWaitRound();
		this.idleCheckDelay = builder.getIdleCheckDelay();
		this.idleCheckPeriod = builder.getIdleCheckPeriod();
		this.monitorLock = new ReentrantLock();
		this.notifier = builder.getNotifier();
	}
	
	public static class NotifierPoolBuilder<Notification> implements Builder<NotifierPool<Notification>>
	{
		// The size of the event queue for each actor. 11/20/2014, Bing Li
		private int queueSize;
		// The count of actors to be managed. 11/20/2014, Bing Li
		private int notifierSize;
		// The time to be waited when no messages are available in the class. 11/20/2014, Bing Li
		private long poolingWaitTime;
		// The time to be waited when no messages are available in each actor. 11/20/2014, Bing Li
		private long notifierWaitTime;
		// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
		private long idleCheckDelay;
		// The idle-checking period. 01/20/2016, Bing Li
		private long idleCheckPeriod;
		// When the actor has no messages to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the actor has killed after no messages are available for sufficient time. 01/20/2016, Bing Li 
//		private int waitRound;
		
		// The scheduler's thread pool size. 05/11/2017, Bing Li
		private int schedulerPoolSize;
		// The time to keep alive for threads in the scheduler. 05/11/2017, Bing Li
		private long schedulerKeepAliveTime;
		private Notifier<Notification> notifier;
		
		public NotifierPoolBuilder()
		{
		}
		
		public NotifierPoolBuilder<Notification> queueSize(int queueSize)
		{
			this.queueSize = queueSize;
			return this;
		}
		
		public NotifierPoolBuilder<Notification> notifierSize(int notifierSize)
		{
			this.notifierSize = notifierSize;
			return this;
		}

		public NotifierPoolBuilder<Notification> poolingWaitTime(long poolingWaitTime)
		{
			this.poolingWaitTime = poolingWaitTime;
			return this;
		}

		public NotifierPoolBuilder<Notification> notifierWaitTime(long notifierWaitTime)
		{
			this.notifierWaitTime = notifierWaitTime;
			return this;
		}

		public NotifierPoolBuilder<Notification> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public NotifierPoolBuilder<Notification> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		/*
		public ActorPoolBuilder<Message> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		public NotifierPoolBuilder<Notification> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public NotifierPoolBuilder<Notification> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		
		public NotifierPoolBuilder<Notification> notifier(Notifier<Notification> notifier)
		{
			this.notifier = notifier;
			return this;
		}

		@Override
		public NotifierPool<Notification> build()
		{
			return new NotifierPool<Notification>(this);
		}
		
		public Notifier<Notification> getNotifier()
		{
			return this.notifier;
		}
		
		public int getQueueSize()
		{
			return this.queueSize;
		}
		
		public int getNotifierSize()
		{
			return this.notifierSize;
		}
		
		public long getPoolingWaitTime()
		{
			return this.poolingWaitTime;
		}
		
		public long getNotifierWaitTime()
		{
			return this.notifierWaitTime;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}

		/*
		public int getWaitRound()
		{
			return this.waitRound;
		}
		*/

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}
	}

	/*
	 * The actor manager cannot hold on forever. Thus, it is possible that it is down when it is to be reused is down. Thus, it needs to check and restart it. 01/20/2016, Bing Li
	 */
	public synchronized void restart()
	{
		// Reset the collaborator for the new resumption. 02/01/2016, Bing Li
		this.collaborator.reset();
		// Check whether the collection to keep actors are disposed. 01/20/2016, Bing Li
		if (this.threads == null)
		{
			// Initialize the collection to keep actors. 01/20/2016, Bing Li
			this.threads = new ConcurrentHashMap<String, Runner<NotificationThread<Notification>>>();
		}
		// Check whether the message queue is disposed. 01/20/2016, Bing Li
		if (this.notificationQueue == null)
		{
			// Initialize the message queue. 01/20/2016, Bing Li
			this.notificationQueue = new LinkedBlockingQueue<Notification>();
		}
		// Schedule the idle-checking task. 01/20/2016, Bing Li
		this.setIdleChecker(this.idleCheckDelay, this.idleCheckPeriod);
	}

	/*
	 * Check whether the actor manager is ready or not. 01/20/2016, Bing Li
	 */
	public synchronized boolean isReady()
	{
		// Check whether the dispatcher is shut down. 01/14/2016, Bing Li
		if (!this.collaborator.isShutdown())
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
	 * Dispose the actor dispatcher. 11/20/2014, Bing Li
	 */
	public synchronized void dispose() throws InterruptedException
	{
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Clear the message queue. 11/20/2014, Bing Li
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
		// Dispose all of actors created during the dispatcher's running procedure. 11/20/2014, Bing Li
		for (Runner<NotificationThread<Notification>> entry : this.threads.values())
		{
			entry.stop();
		}
		// Clear the actor map. 11/20/2014, Bing Li
		this.threads.clear();
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of actors within the dispatcher. 11/20/2014, Bing Li
	 */
	@Override
	public void checkIdle() throws InterruptedException
	{
		// Check each actor managed by the dispatcher. 11/20/2014, Bing Li
		for (Runner<NotificationThread<Notification>> entry : this.threads.values())
		{
			// If the actor is empty and idle, it is the one to be checked. 11/20/2014, Bing Li
			if (entry.getFunction().isEmpty() && entry.getFunction().isIdle())
			{
				// The algorithm to determine whether an actor should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/20/2014, Bing Li
				this.threads.remove(entry.getFunction().getKey());
				// Dispose the actor. 11/20/2014, Bing Li
				entry.stop();
				// Collect the resource of the actor. 11/20/2014, Bing Li
				entry = null;
			}
		}
	}

	/*
	 * Set the idle checking parameters. 11/20/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		this.idleCheckingTask = Scheduler.PERIOD().getScheduler().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
	}

	/*
	 * Perform the action upon the message asynchronously. 11/20/2014, Bing Li
	 */
	public synchronized void notify(Notification message)
	{
		// With the lock, the message queue keeps synchronized with self-disposing mechanism. 02/01/2016, Bing Li
		this.monitorLock.lock();
		// Put the message into the queue. 11/20/2014, Bing Li
		this.notificationQueue.add(message);
		this.monitorLock.unlock();
		// Signal the potential waiting thread to schedule actors to perform the action. 11/20/2014, Bing Li
		this.collaborator.signal();
	}
	
	
	/*
	 * The thread of the dispatcher is always running until no messages to be processed. If too many messages are received, more actors are created by the pool. If messages are limited, the count of threads created by the dispatcher is also small. It is true that no any threads are alive when no messages are received for a long time. 11/20/2014, Bing Li
	 */
	public void run()
	{
		// Declare a string to keep the selected actor key. 11/20/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// Initialize the currentRound to maintain the outside-most loop count. 01/20/2016, Bing Li
//		int currentRound = 0;
		Runner<NotificationThread<Notification>> runner;
		// The pool usually runs all of the time unless the local node is shutdown. To shutdown the pool, the shutdown flag of the collaborator is set to true. 11/20/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			try
			{
				// Check whether messages are available in the queue. 11/20/2014, Bing Li
				while (!this.notificationQueue.isEmpty())
				{
					// Since all of the actors created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive actors are available. If so, it is possible to assign tasks to them if they are not so busy. 11/20/2014, Bing Li
					while (this.threads.size() > 0)
					{
						// Select the actor whose load is the least and keep the key of the actor. 11/20/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(this.threads);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/20/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/20/2014, Bing Li
							if (this.threads.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the actor's load reaches the maximum value. 11/20/2014, Bing Li
									if (this.threads.get(selectedThreadKey).getFunction().isFull())
									{
										// Check if the pool is full. If the least load actor is full as checked by the above condition, it denotes that all of the current alive actors are full. So it is required to create an actor to respond the newly received messages if the actor count of the pool does not reach the maximum. 11/20/2014, Bing Li
										if (this.threads.size() < this.notifierSize)
										{
											// Create a new actor. 11/20/2014, Bing Li
											NotificationThread<Notification> thread = new NotificationThread<Notification>(this.queueSize, this.notifierWaitTime, this.notifier);
											// Create an instance of Runner. 05/20/2018, Bing Li
											runner = new Runner<NotificationThread<Notification>>(thread);
											// Start the runner. 05/20/2018, Bing Li
											runner.start();
											// Save the newly created actor into the map. 11/20/2014, Bing Li
											this.threads.put(thread.getKey(), runner);
											// Enqueue the message into the queue of the newly created actor. Then, the message will be processed by the actor. 11/20/2014, Bing Li
											this.threads.get(thread.getKey()).getFunction().enqueue(this.notificationQueue.poll());
										}
										else
										{
											// Force to put the message into the queue when the count of actors reaches the upper limit and each of the actor's queue is full. 11/20/2014, Bing Li
											this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
										}
									}
									else
									{
										// If the least load actor's queue is not full, just put the message into the queue. 11/20/2014, Bing Li
										this.threads.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
									}

									// Jump out from the loop since the message is put into a thread. 11/20/2014, Bing Li
									break;
								}
								catch (NullPointerException e)
								{
									// Since no concurrency is applied here, it is possible that a NullPointerException is raised. If so, it means that the selected actor is not available. Just continue to select anther one. 11/20/2014, Bing Li
									continue;
								}
							}
						}
					}
					// If no actors are available, it needs to create a new one to take the message. 11/20/2014, Bing Li
					if (this.threads.size() <= 0)
					{
						// Create a new actor. 11/20/2014, Bing Li
						NotificationThread<Notification> thread = new NotificationThread<Notification>(this.queueSize, this.notifierWaitTime, this.notifier);
						// Create an instance of Runner. 05/20/2018, Bing Li
						runner = new Runner<NotificationThread<Notification>>(thread);
						// Start the runner. 05/20/2018, Bing Li
						runner.start();
						// Put it into the map for further reuse. 11/20/2014, Bing Li
						this.threads.put(thread.getKey(), runner);
						// Take the message. 11/20/2014, Bing Li
//						this.threadPool.execute(this.actors.get(thread.getKey()));
						// Start the thread. 11/20/2014, Bing Li
						this.threads.get(thread.getKey()).getFunction().enqueue(this.notificationQueue.poll());
					}

					// If the dispatcher is shutdown, it is not necessary to keep processing the messages. So, jump out the loop and the actor is dead. 11/20/2014, Bing Li
					if (this.collaborator.isShutdown())
					{
						break;
					}
				}

				// Check whether the dispatcher is shutdown or not. 11/20/2014, Bing Li
				if (!this.collaborator.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no messages are available temporarily. Just wait for a while. 11/20/2014, Bing Li
					this.collaborator.holdOn(this.poolingWaitTime);
					// The lock keeps synchronized between the messageQueue and the self-disposing mechanism. When checking whether the dispatcher should be disposed, it needs to protect the dispatcher from receiving additional messages. 02/01/2016, Bing Li
					this.monitorLock.lock();
					try
					{
						// Detect whether the message is empty. 01/20/2016, Bing Li
						if (this.notificationQueue.size() <= 0)
						{
							// Check whether the waitRound is infinite. 01/20/2016, Bing Li
							/*
							if (this.waitRound != UtilConfig.INFINITE_WAIT_ROUND)
							{
								// Check whether the outside-most loop reaches the upper limit. 01/20/2016, Bing Li
								if (currentRound++ >= this.waitRound)
								{
									// Check whether all of the actors are disposed. 01/20/2016, Bing Li
									if (this.actors.isEmpty())
									{
										// Dispose the actor manager. 01/20/2016, Bing Li
										this.dispose();
										// Quit the outside-most loop. 01/20/2016, Bing Li
										break;
									}
								}
							}
							*/
							// Check whether all of the actors are disposed. 01/20/2016, Bing Li
							/*
							if (this.actors.isEmpty())
							{
								// Dispose the actor manager. 01/20/2016, Bing Li
								this.dispose();
								// Quit the outside-most loop. 01/20/2016, Bing Li
								break;
							}
							*/
						}
					}
					finally
					{
						this.monitorLock.unlock();
					}
				}
			}
			/*
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			*/
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}
}
