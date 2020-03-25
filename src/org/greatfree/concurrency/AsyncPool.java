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
public class AsyncPool<Message> extends Thread implements CheckIdleable
{
	// The actors that are available to sent the messages concurrently.. 11/20/2014, Bing Li
	private Map<String, Runner<AsyncActor<Message>>> actors;
	// The queue which contains the message to be processed. 11/20/2014, Bing Li
	private Queue<Message> messageQueue;
	// The size of the message queue for each actor. 11/20/2014, Bing Li
	private final int messageQueueSize;
	// The count of actors to be managed. 11/20/2014, Bing Li
	private final int actorSize;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// The idle checker to monitor whether an actor is idle long enough. 11/20/2014, Bing Li
	private IdleChecker<AsyncPool<Message>> idleChecker;
	// The collaborator is used to pause the dispatcher when no messages are available and notify to continue when new messages are received. 11/20/2014, Bing Li
	private Sync collaborator;
	// The time to be waited when no messages are available in the class. 11/20/2014, Bing Li
	private final long poolingWaitTime;
	// The time to be waited when no messages are available in each actor. 11/20/2014, Bing Li
	private final long actorWaitTime;
	// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
	private final long idleCheckDelay;
	// The idle-checking period. 01/20/2016, Bing Li
	private final long idleCheckPeriod;
	// When the actor has no messages to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the actor has killed after no messages are available for sufficient time. 01/20/2016, Bing Li 
	private final int waitRound;
	// When self-disposing the dispatcher to save resources, it needs to keep synchronization between the message queue and the disposing. The lock is responsible for that. 02/01/2016, Bing Li
	private ReentrantLock monitorLock;
	// The actor which contains the function to be executed asynchronously. 09/10/2018, Bing Li
	private Async<Message> actor;

	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
	public AsyncPool(ActorPoolBuilder<Message> builder)
	{
		this.actors = new ConcurrentHashMap<String, Runner<AsyncActor<Message>>>();
		this.messageQueue = new LinkedBlockingQueue<Message>();
		this.messageQueueSize = builder.getMessageQueueSize();
		this.actorSize = builder.getActorSize();

		Scheduler.GREATFREE().init(builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());

		this.idleChecker = new IdleChecker<AsyncPool<Message>>(this);
		this.collaborator = new Sync(true);
		this.poolingWaitTime = builder.getPoolingWaitTime();
		this.actorWaitTime = builder.getActorWaitTime();
		this.waitRound = builder.getWaitRound();
		this.idleCheckDelay = builder.getIdleCheckDelay();
		this.idleCheckPeriod = builder.getIdleCheckPeriod();
		this.monitorLock = new ReentrantLock();
		this.actor = builder.getActor();
	}
	
	public static class ActorPoolBuilder<Message> implements Builder<AsyncPool<Message>>
	{
		// The size of the event queue for each actor. 11/20/2014, Bing Li
		private int messageQueueSize;
		// The count of actors to be managed. 11/20/2014, Bing Li
		private int actorSize;
		// The time to be waited when no messages are available in the class. 11/20/2014, Bing Li
		private long poolingWaitTime;
		// The time to be waited when no messages are available in each actor. 11/20/2014, Bing Li
		private long actorWaitTime;
		// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
		private long idleCheckDelay;
		// The idle-checking period. 01/20/2016, Bing Li
		private long idleCheckPeriod;
		// When the actor has no messages to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the actor has killed after no messages are available for sufficient time. 01/20/2016, Bing Li 
		private int waitRound;
		
		// The scheduler's thread pool size. 05/11/2017, Bing Li
		private int schedulerPoolSize;
		// The time to keep alive for threads in the scheduler. 05/11/2017, Bing Li
		private long schedulerKeepAliveTime;
		private Async<Message> actor;
		
		public ActorPoolBuilder()
		{
		}
		
		public ActorPoolBuilder<Message> messageQueueSize(int messageQueueSize)
		{
			this.messageQueueSize = messageQueueSize;
			return this;
		}
		
		public ActorPoolBuilder<Message> actorSize(int actorSize)
		{
			this.actorSize = actorSize;
			return this;
		}

		public ActorPoolBuilder<Message> poolingWaitTime(long poolingWaitTime)
		{
			this.poolingWaitTime = poolingWaitTime;
			return this;
		}

		public ActorPoolBuilder<Message> actorWaitTime(long actorWaitTime)
		{
			this.actorWaitTime = actorWaitTime;
			return this;
		}

		public ActorPoolBuilder<Message> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public ActorPoolBuilder<Message> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public ActorPoolBuilder<Message> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}

		public ActorPoolBuilder<Message> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public ActorPoolBuilder<Message> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		
		public ActorPoolBuilder<Message> actor(Async<Message> actor)
		{
			this.actor = actor;
			return this;
		}

		@Override
		public AsyncPool<Message> build()
		{
			return new AsyncPool<Message>(this);
		}
		
		public Async<Message> getActor()
		{
			return this.actor;
		}
		
		public int getMessageQueueSize()
		{
			return this.messageQueueSize;
		}
		
		public int getActorSize()
		{
			return this.actorSize;
		}
		
		public long getPoolingWaitTime()
		{
			return this.poolingWaitTime;
		}
		
		public long getActorWaitTime()
		{
			return this.actorWaitTime;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}
		
		public int getWaitRound()
		{
			return this.waitRound;
		}

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
		if (this.actors == null)
		{
			// Initialize the collection to keep actors. 01/20/2016, Bing Li
			this.actors = new ConcurrentHashMap<String, Runner<AsyncActor<Message>>>();
		}
		// Check whether the message queue is disposed. 01/20/2016, Bing Li
		if (this.messageQueue == null)
		{
			// Initialize the message queue. 01/20/2016, Bing Li
			this.messageQueue = new LinkedBlockingQueue<Message>();
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
		if (this.messageQueue != null)
		{
			this.messageQueue.clear();
		}
		// Detect whether the idle-checking task is initialized. 02/01/2016, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Cancel the idle-checking task. 02/01/2016, Bing Li
			this.idleCheckingTask.cancel(true);
		}
		// Dispose all of actors created during the dispatcher's running procedure. 11/20/2014, Bing Li
		for (Runner<AsyncActor<Message>> actor : this.actors.values())
		{
			actor.stop();
		}
		// Clear the actor map. 11/20/2014, Bing Li
		this.actors.clear();
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of actors within the dispatcher. 11/20/2014, Bing Li
	 */
	@Override
	public void checkIdle() throws InterruptedException
	{
		// Check each actor managed by the dispatcher. 11/20/2014, Bing Li
		for (Runner<AsyncActor<Message>> entry : this.actors.values())
		{
			// If the actor is empty and idle, it is the one to be checked. 11/20/2014, Bing Li
			if (entry.getFunction().isEmpty() && entry.getFunction().isIdle())
			{
				// The algorithm to determine whether an actor should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/20/2014, Bing Li
				this.actors.remove(entry.getFunction().getKey());
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
		this.idleCheckingTask = Scheduler.GREATFREE().getScheduler().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
	}

	/*
	 * Perform the action upon the message asynchronously. 11/20/2014, Bing Li
	 */
	public synchronized void perform(Message message)
	{
		// With the lock, the message queue keeps synchronized with self-disposing mechanism. 02/01/2016, Bing Li
		this.monitorLock.lock();
		// Put the message into the queue. 11/20/2014, Bing Li
		this.messageQueue.add(message);
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
		int currentRound = 0;
		Runner<AsyncActor<Message>> runner;
		// The pool usually runs all of the time unless the local node is shutdown. To shutdown the pool, the shutdown flag of the collaborator is set to true. 11/20/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			try
			{
				// Check whether messages are available in the queue. 11/20/2014, Bing Li
				while (!this.messageQueue.isEmpty())
				{
					// Since all of the actors created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive actors are available. If so, it is possible to assign tasks to them if they are not so busy. 11/20/2014, Bing Li
					while (this.actors.size() > 0)
					{
						// Select the actor whose load is the least and keep the key of the actor. 11/20/2014, Bing Li
						selectedThreadKey = CollectionSorter.minValueKey(this.actors);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/20/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/20/2014, Bing Li
							if (this.actors.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the actor's load reaches the maximum value. 11/20/2014, Bing Li
									if (this.actors.get(selectedThreadKey).getFunction().isFull())
									{
										// Check if the pool is full. If the least load actor is full as checked by the above condition, it denotes that all of the current alive actors are full. So it is required to create an actor to respond the newly received messages if the actor count of the pool does not reach the maximum. 11/20/2014, Bing Li
										if (this.actors.size() < this.actorSize)
										{
											// Create a new actor. 11/20/2014, Bing Li
											AsyncActor<Message> thread = new AsyncActor<Message>(this.messageQueueSize, this.actorWaitTime, this.actor);
											// Create an instance of Runner. 05/20/2018, Bing Li
											runner = new Runner<AsyncActor<Message>>(thread);
											// Start the runner. 05/20/2018, Bing Li
											runner.start();
											// Save the newly created actor into the map. 11/20/2014, Bing Li
											this.actors.put(thread.getKey(), runner);
											// Enqueue the message into the queue of the newly created actor. Then, the message will be processed by the actor. 11/20/2014, Bing Li
											this.actors.get(thread.getKey()).getFunction().enqueue(this.messageQueue.poll());
										}
										else
										{
											// Force to put the message into the queue when the count of actors reaches the upper limit and each of the actor's queue is full. 11/20/2014, Bing Li
											this.actors.get(selectedThreadKey).getFunction().enqueue(this.messageQueue.poll());
										}
									}
									else
									{
										// If the least load actor's queue is not full, just put the message into the queue. 11/20/2014, Bing Li
										this.actors.get(selectedThreadKey).getFunction().enqueue(this.messageQueue.poll());
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
					if (this.actors.size() <= 0)
					{
						// Create a new actor. 11/20/2014, Bing Li
						AsyncActor<Message> thread = new AsyncActor<Message>(this.messageQueueSize, this.actorWaitTime, this.actor);
						// Create an instance of Runner. 05/20/2018, Bing Li
						runner = new Runner<AsyncActor<Message>>(thread);
						// Start the runner. 05/20/2018, Bing Li
						runner.start();
						// Put it into the map for further reuse. 11/20/2014, Bing Li
						this.actors.put(thread.getKey(), runner);
						// Take the message. 11/20/2014, Bing Li
//						this.threadPool.execute(this.actors.get(thread.getKey()));
						// Start the thread. 11/20/2014, Bing Li
						this.actors.get(thread.getKey()).getFunction().enqueue(this.messageQueue.poll());
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
						if (this.messageQueue.size() <= 0)
						{
							// Check whether the waitRound is infinite. 01/20/2016, Bing Li
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
						}
					}
					finally
					{
						this.monitorLock.unlock();
					}
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
