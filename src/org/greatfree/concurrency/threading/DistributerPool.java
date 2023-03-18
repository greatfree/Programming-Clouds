package org.greatfree.concurrency.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.greatfree.concurrency.threading.message.ATMNotification;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.data.ServerConfig;

// Created: 09/13/2019, Bing Li
class DistributerPool
{
	private NotificationDispatcher<ATMNotification, ATMThread, ATMThreadCreator> pool;

	private DistributerPool()
	{
	}
	
	private static DistributerPool instance = new DistributerPool();
	
	public static DistributerPool POOL()
	{
		if (instance == null)
		{
			instance = new DistributerPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws InterruptedException
	{
		this.pool.dispose();
	}
	
	public void init(ScheduledThreadPoolExecutor scheduler)
	{
		this.pool = new NotificationDispatcher.NotificationDispatcherBuilder<ATMNotification, ATMThread, ATMThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ATMThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(scheduler)
				.build();
	}

	public String generateThread()
	{
		return this.pool.generateThread();
	}
	
	public Set<String> generateThreads(int n)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
		String threadKey;
		for (int i = 0; i < n; i++)
		{
			threadKey = this.generateThread();
			if (threadKey != null)
			{
				keys.add(threadKey);
			}
		}
		System.out.println("DistributerPool-generateThreads(): keys size = " + keys.size());
		return keys;
	}
	
	public void enqueueInstruction(ATMNotification notification)
	{
		if (!notification.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			this.pool.enqueueInstruction(notification.getThreadKey(), notification);
		}
		else
		{
			for (String entry : notification.getThreadKeys())
			{
				this.pool.enqueueInstruction(entry, notification);
			}
		}
	}

	/*
	 * The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
	 *
	public void signal(String threadKey)
	{
		this.pool.signal(threadKey);
	}
	*/
	
	public boolean isAlive(String threadKey)
	{
		return this.pool.isAlive(threadKey);
	}
	
	public void execute(String threadKey)
	{
		this.pool.execute(threadKey);
	}
	
	public void invoke(TaskInvokeNotification notification)
	{
		if (!notification.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.pool.isAlive(notification.getThreadKey()))
			{
				this.pool.execute(notification.getThreadKey());
			}
		}
		else
		{
			for (String entry : notification.getThreadKeys())
			{
				if (!this.pool.isAlive(entry))
				{
					this.pool.execute(entry);
				}
			}
		}
		this.enqueueInstruction((ATMNotification)notification);
	}
	
	public void invoke(TaskInvokeRequest request)
	{
		if (!request.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.pool.isAlive(request.getThreadKey()))
			{
				this.pool.execute(request.getThreadKey());
			}
		}
		else
		{
			for (String entry : request.getThreadKeys())
			{
				if (!this.pool.isAlive(entry))
				{
					this.pool.execute(entry);
				}
			}
		}
		this.enqueueInstruction((ATMNotification)request);
	}
	
	public void invoke(InteractNotification notification)
	{
		if (!notification.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.pool.isAlive(notification.getThreadKey()))
			{
				this.pool.execute(notification.getThreadKey());
			}
		}
		else
		{
			for (String entry : notification.getThreadKeys())
			{
				if (!this.pool.isAlive(entry))
				{
					this.pool.execute(entry);
				}
			}
		}
		this.enqueueInstruction((ATMNotification)notification);
	}
	
	public void invoke(InteractRequest request)
	{
		/*
		if (request == null)
		{
			System.out.println("DistributerPool-invoke(): request is NULL");
		}
		else
		{
			System.out.println("DistributerPool-invoke(): request is NOT NULL");
		}
		*/
		if (!request.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.pool.isAlive(request.getThreadKey()))
			{
				this.pool.execute(request.getThreadKey());
			}
		}
		else
		{
			for (String entry : request.getThreadKeys())
			{
				if (!this.pool.isAlive(entry))
				{
					this.pool.execute(entry);
				}
			}
		}
		this.enqueueInstruction((ATMNotification)request);
	}
	
	public void kill(String threadKey, long timeout) throws InterruptedException
	{
		this.pool.shutdown(threadKey, timeout);
	}
	
	public void killAll(long timeout) throws InterruptedException
	{
		this.pool.shutdownAll(timeout);
	}
}
