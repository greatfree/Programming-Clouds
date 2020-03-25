package org.greatfree.concurrency.mapreduce;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.util.Tools;

/*
 * The class is the thread to perform Map/Reduce. It has a queue whose length should be as small as possible if raising the performance is the goal.
 */

// Created: 04/19/2018, Bing Li
// public abstract class MapReduceQueue<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>> implements Comparable<MapReduceQueue<Task, Result, TaskThread, ThreadCreator>>, Runnable
public abstract class MapReduceQueue<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>> extends RunnerTask implements Comparable<MapReduceQueue<Task, Result, TaskThread, ThreadCreator>>
{
	// The key that represents the thread that works on the tasks. 04/21/2018, Bing Li
	private String key;
	// The queue to retain the tasks for the thread to work on. 04/21/2018, Bing Li
	private Queue<Task> queue;
	// The size of the tasks. 04/21/2018, Bing Li
	private int taskSize;
	// The notify/wait synchronization to collaborate the task production and the task consumption. 04/21/2018, Bing Li
	private Sync collaborator;
	// The flag to indicate the state, idle or busy, of the thread. 04/21/2018, Bing Li
	private boolean isIdle;
	// The reference to the map/reduce management instance of MapCore. 04/21/2018, Bing Li
	private MRCore<Task, Result, TaskThread, ThreadCreator> mp;
	// The lock to manage the consistency of idle-state of the thread. 04/21/2018, bing Li
	private ReentrantLock idleLock;
	
	// The thread is possibly interrupted by the thread pool/the system when the thread is hung by a task permanently. If so, the exception is not required to be displayed according to the flag. 11/05/2019, Bing Li
//	private AtomicBoolean isSysInterrupted;
	private AtomicBoolean isHung;

	/*
	 * The constructor of the thread. Two parameters are required, i.e., the task size and the reference to the map/reduce management instance of MapCore. 04/21/2018, Bing Li
	 */
	public MapReduceQueue(int taskSize, MRCore<Task, Result, TaskThread, ThreadCreator> mp)
	{
		// Generate the unique key that represents the thread. 04/21/2018, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue that retains the tasks. 04/21/2018, Bing Li
		this.queue = new LinkedBlockingQueue<Task>();
		// The size of the tasks. 04/21/2018, Bing Li
		this.taskSize = taskSize;
		// Initialize the notify/wait collaborator. 04/21/2018, Bing Li
		this.collaborator = new Sync(false);
		// Initialize the idle state as false. 04/21/2018, Bing Li
		this.isIdle = false;
		// Keep the reference to the map/reduce management instance of MapCore. 04/21/2018, Bing Li
		this.mp = mp;
		// Initialize the idle consistency lock. 04/21/2018, Bing Li
		this.idleLock = new ReentrantLock();
//		this.isSysInterrupted = new AtomicBoolean(false);
		this.isHung = new AtomicBoolean(false);
	}

	/*
	 * Dispose the resources of the thread. 04/21/2018, Bing Li
	 */
	public synchronized void dispose() throws InterruptedException
	{
		// Set the shutdown and notify all of the relevant waiting threads. 04/21/2018, Bing Li
		this.collaborator.shutdown();
		// Check whether the task queue is NULL or not. 04/21/2018, Bing Li
		if (this.queue != null)
		{
			// Clear the queue. 04/21/2018, Bing Li
			this.queue.clear();
		}
		/*
		if (this.isHung.get())
		{
//			System.out.println("MapReduceQueue is interrupted!");
			this.interrupt();
		}
		*/
	}
	
	/*
	 * Expose the key of the thread. 04/21/2018, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * It is useless and incorrect to interrupt a thread this way. It should be done in the runner, which has a method, interrupt(). 11/07/2019, Bing Li
	 * 
	 * The method aims to kill one thread that is hung permanently by a task. 11/05/2019, Bing Li
	 */
	/*
	public void interrupt()
	{
		this.isSysInterrupted.set(true);
		Thread.currentThread().interrupt();
	}
	*/
	
	/*
	public boolean isSysInterrupted()
	{
		return this.isSysInterrupted.get();
	}
	*/
	
	public boolean isInterrupted()
	{
		return Thread.currentThread().isInterrupted();
	}
	
	/*
	public void setHung(boolean isHung)
	{
		this.isHung.set(isHung);
	}
	*/

	public boolean isHung()
	{
		return this.isHung.get();
	}

	/*
	 * Enqueue the task for the thread to process. 04/21/2018, Bing Li
	 */
	public void enqueue(Task notification)
	{
		// Set the lock to keep the consistency of setting the value of idle state. 04/21/2018, Bing Li
		this.idleLock.lock();
		// When a task is available for processing, it needs to set the idle state of the thread as false. 04/21/2018, Bing Li
		this.isIdle = false;
		// Enqueue the task into the task queue. 04/21/2018, Bing Li
		this.queue.add(notification);
		// The end of the lock. 04/21/2018, Bing Li
		this.idleLock.unlock();
		// Notify the consumption thread to start to work on the task. 04/21/2018, Bing Li
		this.collaborator.signalAll();
	}

	/*
	 * When no tasks are available, it is time to wait for some time before shutting down the thread. 04/21/2018, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Set the lock to keep the consistency of setting the value of idle state. 04/21/2018, Bing Li
		this.idleLock.lock();
		// Before waiting for long enough, the idle-state of the thread is still false. 04/21/2018, Bing Li
		this.isIdle = false;
		// The end of the lock. 04/21/2018, Bing Li
		this.idleLock.unlock();
		// Wait for some time. 04/21/2018, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the lock to keep the consistency of setting the value of idle state. 04/21/2018, Bing Li
		this.idleLock.lock();
		// Check whether any tasks are available for processing. 04/21/2018, Bing Li
		if (this.queue.size() <= 0)
		{
			// If no tasks are available and it has waited for long enough, the idle-state of the thread is true. 04/21/2018, Bing Li
			this.isIdle = true;
		}
		// The end of the lock. 04/21/2018, Bing Li
		this.idleLock.unlock();
	}

	/*
	 * Check whether the thread is shutdown or not. 04/21/2018, Bing Li
	 */
	public boolean isShutdown()
	{
		// Return the shutdown-state of the thread. 04/21/2018, Bing Li
		return this.collaborator.isShutdown();
	}

	/*
	 * Check whether the thread's task queue is full or not. 04/21/2018, Bing Li
	 */
	public boolean isFull()
	{
		// If the size of the task queue is larger than the task size, it indicates that the task queue is full. 04/21/2018, Bing Li
		return this.queue.size() >= this.taskSize;
	}

	/*
	 * Check whether the task queue is empty or not. 04/21/2018, Bing Li
	 */
	public boolean isEmpty()
	{
		// Check whether the task queue is null or not. 04/21/2018, Bing Li
		if (this.queue != null)
		{
			// If the task queue size is less than zero, it represents that the task queue is empty. Otherwise, it is not empty. 04/21/2018, Bing Li
			return this.queue.size() <= 0;
		}
		// If the task queue is null, it indicates that the task queue is empty as well. 04/21/2018, Bing Li
		return true;
	}
	
	/*
	 * Check whether the thread is idle or not. 04/21/2018, Bing Li
	 */
	public synchronized boolean isIdle()
	{
		// Set the lock to keep the consistency of reading the value of idle state. 04/21/2018, Bing Li
		this.idleLock.lock();
		try
		{
			// Check whether the task queue size is less than or equal to zero or not. 04/21/2018, Bing Li
			if (this.queue.size() <= 0)
			{
				// If the queue size is less than or equal to zero, return the idle-state. 04/21/2018, Bing Li
				return this.isIdle;
			}
			else
			{
				// If the queue size is large than zero, the thread is not idle. 04/21/2018, Bing Li
				return false;
			}
		}
		finally
		{
			// The end of the lock. 04/21/2018, Bing Li
			this.idleLock.unlock();
		}
	}

	/*
	 * Get the task queue size of the thread. 04/21/2018, Bing Li
	 */
	public int getQueueSize()
	{
		return this.queue.size();
	}

	/*
	 * Peek the first task of the task queue. 04/21/2018, Bing Li
	 */
	public Task peekNotification()
	{
		return this.queue.peek();
	}

	/*
	 * Dequeue the first task of the task queue. 04/21/2018, Bing Li
	 */
	public Task getTask() throws InterruptedException
	{
		this.isHung.set(true);
		return this.queue.poll();
	}

	/*
	 * Reduce the result. 04/21/2018, Bing Li
	 */
	public void reduce(Result result)
	{
		this.isHung.set(false);
		// Reduce the result through invoking the method, reduce, of the reference of the map/reduce management instance of MapCore. 04/21/2018, Bing Li
		this.mp.reduce(result);
	}

	/*
	 * Dispose the task to save the space it occupies. 04/21/2018, Bing Li
	 */
	public synchronized void disposeObject(Task notification)
	{
		notification = null;
	}

	/*
	 * The thread implements the interface, Comparable. It enables the thread comparable. In the case, the thread is comparable in terms of the size of its task queue. The larger the size of the task queue, the larger the thread. 04/21/2018, Bing Li
	 */
	@Override
	public int compareTo(MapReduceQueue<Task, Result, TaskThread, ThreadCreator> obj)
	{
		if (obj != null)
		{
			if (this.queue.size() > obj.getQueueSize())
			{
				return 1;
			}
			else if (this.queue.size() == obj.getQueueSize())
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 1;
		}
	}

}
