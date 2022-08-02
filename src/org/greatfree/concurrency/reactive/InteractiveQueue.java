package org.greatfree.concurrency.reactive;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

/*
 * This is the thread pool to manage the thread, InteractiveQueue, particularly. Other than scheduling tasks to idle or lower-load threads, the thread pool has to interact with the threads which it maintains before making any decisions. Traditionally, it assumes that each task has the same workload. Thus, it is unnecessary to perform interactions between a thread pool and its threads. However, in a more complicated computing environment, each task might be heterogeneous in terms of the resource consumption. And, it is impossible to know that prior to the task being taken. Thus, the interaction during the procedure of concurrent running is mandatory for the thread pool to manage those threads. For lightweight tasks, a thread can take a large number of such ones. For heavyweight tasks, it is unreasonable to assign it with many tasks. If one task takes time that has been executed for long enough or consumed high resources, it is necessary to terminate the thread. 05/24/2017, Bing Li
 */

/*
 * This is a more complicated thread. Besides the feature of NotificationObjectQueue, it is able to interact with its thread pool, i.e., InteractiveDispatcher, to notify its current status and even make a decision based on its status. In contrast, the thread pool can determine how to schedule tasks to those threads and how to maintain the life cycle of those threads in accordance with the status notifications from the thread. It is made use of the case when the work load is high and each of tasks can hardly be evaluated heavy or light prior to being executed. Thus, running status has to be notified by the thread itself after working on the load for some time. 05/24/2017, Bing Li
 */


/*
 * This is the base class that must be derived to implement a thread that holds the methods which could be called to notify the interactive dispatcher. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
// public abstract class InteractiveQueue<Task, Notifier extends Interactable<Task>> implements Runnable
public abstract class InteractiveQueue<Task, Notifier extends Interactable<Task>> extends RunnerTask
{
	// The key to represent the thread. 11/20/2014, Bing Li
	private final String key;
	// The queue to keep tasks that should be processed by the thread. 11/20/2014, Bing Li
	private Queue<Task> queue;
	// The maximum task size. 11/20/2014, Bing Li
	private final int taskSize;
	// The notify/wait mechanism to coordinate the thread. 11/20/2014, Bing Li
	private Sync collaborator;
	// The notifier that interacts with the callee. 11/20/2014, Bing Li
	private Notifier notifier;
	// The starting time of the thread. 11/20/2014, Bing Li
	private Date startTime;
	// The ending time of the thread. 11/20/2014, Bing Li
	private Date endTime;
	// The idle time of the thread. 11/20/2014, Bing Li
	private Date idleTime;
	// The current task the thread is working on. 11/20/2014, Bing Li
	private Task currentTask;
	// The read/write lock to manage the concurrency on the thread. 11/20/2014, Bing Li
	private ReentrantReadWriteLock lock;

	/*
	 * Initialize. The notifier is defined outside. 11/20/2014, Bing Li
	 */
	public InteractiveQueue(int taskSize, Notifier notifier)
	{
		this.key = Tools.generateUniqueKey();
		this.queue = new LinkedList<Task>();
		this.taskSize = taskSize;
		this.collaborator = new Sync();
		this.notifier = notifier;
		this.startTime = Time.INIT_TIME;
		this.endTime = Time.INIT_TIME;
		this.idleTime = Time.INIT_TIME;
		this.lock = new ReentrantReadWriteLock();
	}

	/*
	 * Dispose the thread. 11/20/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		/*
		// Set the shutdown flag to be true. Thus, the loop in the dispatcher thread to schedule tasks is terminated. 11/21/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the dispatcher thread that is waiting for tasks to terminate the waiting. 11/21/2014, Bing Li 
		this.collaborator.signalAll();
		try
		{
			// Wait for the thread to end. 11/21/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue. 11/21/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	/*
	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue. 11/21/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}
	*/

	/*
	 * Expose the key of the thread. 11/21/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the starting time of the thread. 11/21/2014, Bing Li
	 */
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

	/*
	 * Expose the ending time of the thread. 11/21/2014, Bing Li
	 */
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

	/*
	 * Set the starting time. 11/21/2014, Bing Li
	 */
	public void setStartTime()
	{
		this.lock.writeLock().lock();
		// Set the starting time. 11/21/2014, Bing Li
		this.startTime = Calendar.getInstance().getTime();
		// An initial time is set to the idle time. It denotes that the thread is busy. 11/21/2014, Bing Li
		this.idleTime = Time.INIT_TIME;
		// An initial time is set to the ending time. It denotes that the thread is still in the process of working, not ending yet. 11/21/2014, Bing Li
		this.endTime = Time.INIT_TIME;
		this.lock.writeLock().unlock();
	}

	/*
	 * Set the ending time. 11/21/2014, Bing Li
	 */
	public void setEndTime()
	{
		this.lock.writeLock().lock();
		// An initial time is set to the starting time. It denotes that the thread is ended. 11/21/2014, Bing Li
		this.startTime = Time.INIT_TIME;
		// Set the ending time. 11/21/2014, Bing Li
		this.endTime = Calendar.getInstance().getTime();
		this.lock.writeLock().unlock();
	}

	/*
	 * Enqueue a task to the thread. 11/21/2014, Bing Li
	 */
	public void enqueue(Task task)
	{
		this.lock.writeLock().lock();
		// When a task is enqueued, it indicates that it must not be idle. Thus, the idle time is set to an initial time. 11/21/2014, Bing Li
		this.idleTime = Time.INIT_TIME;
		// Enqueue the new task. 11/21/2014, Bing Li
		this.queue.add(task);
		this.lock.writeLock().unlock();
		// Notify the thread since now it might be waiting for new tasks. 11/21/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Wait for some time. It happens when no tasks are available. 11/21/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time. 11/21/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		this.lock.writeLock().lock();
		// Set the idle time after waiting for some time. It represents that the thread is idle. 11/21/2014, Bing Li
		this.idleTime = Calendar.getInstance().getTime();
		this.lock.writeLock().unlock();
	}

	/*
	 * Check whether the thread is shutdown. 11/21/2014, Bing Li
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	/*
	 * Check whether the queue of the thread is full. 11/21/2014, Bing Li
	 */
	public boolean isFull()
	{
		this.lock.readLock().lock();
		try
		{
			// If the queue size is greater than or equal to the maximum task size, it denotes that the thread is full. 11/21/2014, Bing Li
			return this.queue.size() >= this.taskSize;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	/*
	 * Check whether the queue of the thread is empty. 11/21/2014, Bing Li
	 */
	public boolean isEmpty()
	{
		this.lock.readLock().lock();
		try
		{
			// Check if the queue is valid. 11/21/2014, Bing Li
			if (this.queue != null)
			{
				// Check whether the size of the queue is less than zero. 11/21/2014, Bing Li
				if (this.queue.size() <= 0)
				{
					// If no tasks are available in the queue, it needs to notify the manager, usually a thread pool, of the thread to speed up the thread if it is identified as a slow one at the moment. 11/21/2014, Bing Li
					this.notifier.restoreFast(this.key);
					// Tell the invoker that the thread is empty. 11/21/2014, Bing Li
					return true;
				}
				else
				{
					// Tell the invoker that the thread is not empty. 11/21/2014, Bing Li
					return false;
				}
			}
			// Tell the invoker that the thread is empty. 11/21/2014, Bing Li
			return true;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	/*
	 * Expose the current queue size. 11/21/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
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

	/*
	 * Clear the queue. 11/21/2014, Bing Li
	 */
	public void clear()
	{
		this.lock.writeLock().lock();
		this.queue.clear();
		this.lock.writeLock().unlock();
	}

	/*
	 * Expose the idle time. 11/21/2014, Bing Li
	 */
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

	/*
	 * Expose the current task. 11/21/2014, Bing Li
	 */
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

	/*
	 * Notify the manager, usually a thread pool, of the thread that a specific task is done in the thread. 11/21/2014, Bing Li
	 */
	public void done(Task task)
	{
		this.notifier.done(task);
	}

	/*
	 * Dequeue a task from the thread. 11/21/2014, Bing Li 
	 */
	public Task getTask() throws InterruptedException
	{
		this.lock.writeLock().lock();
		try
		{
			// Check if the queue is valid. 11/21/2014, Bing Li
			if (this.queue != null)
			{
				// Check whether the queue contains tasks. 11/21/2014, Bing Li
				if (!this.queue.isEmpty())
				{
					// Dequeue a task if the queue is not empty. 11/21/2014, Bing Li
					this.currentTask = this.queue.poll();
					// Check whether the queue size is less than the maximum task size. 11/21/2014, Bing Li
					if (this.queue.size() < this.taskSize)
					{
						// Notify the thread manager, usually a thread pool, to let the thread keep on working. It might get stuck for overload. 11/21/2014, Bing Li
						this.notifier.keepOn();
					}
					// Return the task. 11/21/2014, Bing Li
					return this.currentTask;
				}
			}
			// No task is available if the queue is invalid. 11/21/2014, Bing Li
			return null;
		}
		finally
		{
			this.lock.writeLock().unlock();
		}
	}

	/*
	 * Dispose one task. It happens when the task is finished. 11/21/2014, Bing Li
	 */
	public void disposeObject(Task notification)
	{
		notification = null;
	}
}
