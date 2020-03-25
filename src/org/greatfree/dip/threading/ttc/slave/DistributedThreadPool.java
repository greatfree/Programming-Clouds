package org.greatfree.dip.threading.ttc.slave;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.threading.NotificationDispatcher;
import org.greatfree.concurrency.threading.message.InstructNotification;
import org.greatfree.data.ServerConfig;

// Created: 09/11/2019, Bing Li
class DistributedThreadPool
{
	private NotificationDispatcher<InstructNotification, NotificationThread, NotificationThreadCreator> pool;
//	private NotificationDispatcher<SyncInstructNotification, FreeThread, FreeThread> pool;
	
//	private Map<String, ReentrantLock> syncs;
	
	private DistributedThreadPool()
	{
	}
	
	private static DistributedThreadPool instance = new DistributedThreadPool();
	
	public static DistributedThreadPool POOL()
	{
		if (instance == null)
		{
			instance = new DistributedThreadPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose(long timeout) throws InterruptedException
	{
		Scheduler.GREATFREE().shutdown(timeout);
//		this.syncs.clear();
//		this.syncs = null;
		this.pool.dispose();
	}
	
	public void init()
	{
		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

//		this.pool = new NotificationDispatcher.NotificationDispatcherBuilder<SyncInstructNotification, FreeThread, FreeThreadCreator>()
		this.pool = new NotificationDispatcher.NotificationDispatcherBuilder<InstructNotification, NotificationThread, NotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new NotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.build();
		
//		this.syncs = new ConcurrentHashMap<String, ReentrantLock>();
	}

	public String generateThread()
	{
		/*
		String threadKey = this.pool.generateThread();
		this.syncs.put(threadKey, new ReentrantLock());
		return threadKey;
		*/
		return this.pool.generateThread();
	}

	public void enqueueInstruction(InstructNotification notification)
	{
//		this.pool.enqueueInstruction(notification.getThreadKey(), new SyncInstructNotification(notification, this.syncs.get(notification.getThreadKey())));
		this.pool.enqueueInstruction(notification.getThreadKey(), notification);
	}

	/*
	 * The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
	 */
	/*
	public void signal(String threadKey)
	{
		this.syncs.get(threadKey).lock();
		this.pool.signal(threadKey);
		this.syncs.get(threadKey).unlock();
	}
	*/
	
	public boolean isAlive(String threadKey)
	{
		/*
		this.syncs.get(threadKey).lock();
		try
		{
			return this.pool.isAlive(threadKey);
		}
		finally
		{
			this.syncs.get(threadKey).unlock();
		}
		*/
		return this.pool.isAlive(threadKey);
	}
	
	public void execute(String threadKey)
	{
//		this.syncs.get(threadKey).lock();
		this.pool.execute(threadKey);
//		this.syncs.get(threadKey).unlock();
	}
	
	public void kill(String threadKey, long timeout) throws InterruptedException
	{
//		this.syncs.get(threadKey).lock();
		this.pool.shutdown(threadKey, timeout);
//		this.syncs.get(threadKey).unlock();
//		this.syncs.remove(threadKey);
	}
}
