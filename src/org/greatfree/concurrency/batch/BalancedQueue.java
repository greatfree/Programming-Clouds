package org.greatfree.concurrency.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

// Created: 04/23/2018, Bing Li
// public abstract class BalancedQueue<Task, Notifier extends ThreadNotifiable<Task>> implements Runnable
public abstract class BalancedQueue<Task, Notifier extends ThreadNotifiable<Task>> extends RunnerTask
{
	private String key;
	private Queue<Task> queue;
	private int taskSize;
	private Sync collaborator;
	private Notifier notifier;
	private Date startTime;
	private Date endTime;
	private Date idleTime;
	private Task currentTask;
	private ReentrantReadWriteLock lock;
	
	public BalancedQueue(int taskSize, Notifier notifier)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedList<Task>();
		this.taskSize = taskSize;
		this.collaborator = new Sync(false);
		this.notifier = notifier;
		this.startTime = Time.INIT_TIME;
		this.endTime = Time.INIT_TIME;
		this.idleTime = Time.INIT_TIME;
		this.lock = new ReentrantReadWriteLock();
	}
	
//	abstract public void dispose() throws InterruptedException;
	
	abstract public String getTaskInString();
	
	public void shutdown() throws InterruptedException
	{
		this.collaborator.shutdown(this);
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}
	
	public String getKey()
	{
		return this.key;
	}

	public Date getStartTime()
	{
		this.lock.readLock().lock();
		try
		{
			return this.startTime;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public Date getEndTime()
	{
		this.lock.readLock().lock();
		try
		{
			return this.endTime;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void setStartTime()
	{
		this.lock.writeLock().lock();
		this.startTime = Calendar.getInstance().getTime();
		this.idleTime = Time.INIT_TIME;
		this.endTime = Time.INIT_TIME;
		this.lock.writeLock().unlock();
	}
	
	public void setEndTime()
	{
		this.lock.writeLock().lock();
		this.startTime = Time.INIT_TIME;
		this.endTime = Calendar.getInstance().getTime();
		this.lock.writeLock().unlock();
	}
	
	public void enqueue(Task task)
	{
		this.lock.writeLock().lock();
		this.idleTime = Time.INIT_TIME;
		this.queue.add(task);
		this.lock.writeLock().unlock();
		this.collaborator.signal();
	}
	
	public void holdOn(long waitTime) throws InterruptedException
	{
		this.collaborator.holdOn(waitTime);
		this.lock.writeLock().lock();
		if (this.idleTime == Time.INIT_TIME)
		{
			this.idleTime = Calendar.getInstance().getTime();
		}
		this.lock.writeLock().unlock();
	}
	
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	public boolean isFull()
	{
		this.lock.readLock().lock();
		try
		{
			return this.queue.size() >= this.taskSize;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	public boolean isEmpty() throws InterruptedException
	{
		this.lock.readLock().lock();
		try
		{
			if (this.queue != null)
			{
				if (this.queue.size() <= 0)
				{
					this.notifier.restoreFast(this.key);
					return true;
				}
				else
				{
					return false;
				}
			}
			return true;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public int getQueueSize()
	{
		this.lock.readLock().lock();
		try
		{
			return this.queue.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void clear()
	{
		this.lock.writeLock().lock();
		this.queue.clear();
		this.lock.writeLock().unlock();
	}

	public Date getIdleTime()
	{
		this.lock.readLock().lock();
		try
		{
			return this.idleTime;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public Task getCurrentTask()
	{
		this.lock.readLock().lock();
		try
		{
			return this.currentTask;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	
	public void done(Task task)
	{
		this.notifier.done(task);
	}
	
	public Task getTask() throws InterruptedException
	{
		this.lock.writeLock().lock();
		try
		{
			if (this.queue != null)
			{
				if (!this.queue.isEmpty())
				{
					this.currentTask = this.queue.poll();
					if (this.queue.size() < this.taskSize)
					{
						this.notifier.keepOn();
					}
					return this.currentTask;
				}
			}
			return null;
		}
		finally
		{
			this.lock.writeLock().unlock();
		}
	}
	
	public void disposeObject(Task notification)
	{
		notification = null;
	}
}
