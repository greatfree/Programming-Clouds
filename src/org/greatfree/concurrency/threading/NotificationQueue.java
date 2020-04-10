package org.greatfree.concurrency.threading;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Tools;

/*
 * Different from the reactive NotificationQueue, the class can also be controlled remotely by programmers in addition to being managed by NotificationDispatcher. 09/10/2019, Bing Li
 */

// Created: 09/10/2019, Bing Li
public abstract class NotificationQueue<Notification extends ServerMessage> extends RunnerTask
{
	// Declare the key for the notification thread. 11/04/2014, Bing Li
	private final String key;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/04/2014, Bing Li
	private Queue<Notification> queue;
	// Declare the size of the queue. 11/04/2014, Bing Li
	private final int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/04/2014, Bing Li
	private Sync sync;
	// The flag that indicates the busy/idle state of the thread. 11/04/2014, Bing Li
	private boolean isIdle;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;
	
	// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
//	private AtomicBoolean isWaitForEver;
//	private AtomicLong waitTime;

	// The thread is possibly interrupted by the thread pool/the system when the thread is hung by a task permanently. If so, the exception is not required to be displayed according to the flag. 11/05/2019, Bing Li
	private AtomicBoolean isHung;

	/*
	 * Initialize the notification thread. This constructor has a limit on the size of the queue. 11/04/2014, Bing Li
	 */
	public NotificationQueue(int taskSize)
	{
		// Generate the key. 11/04/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue with the particular size constraint. 11/04/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Notification>();
		// Set the value of taskSize. 11/04/2014, Bing Li
		this.taskSize = taskSize;
		// Initialize the collaborator. 11/04/2014, Bing Li
		this.sync = new Sync();
		// Set the idle state to false. 11/04/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
		
//		this.isWaitForEver = new AtomicBoolean(false);
//		this.waitTime = new AtomicLong(0);
		this.isHung = new AtomicBoolean(false);
	}

	/*
	 * Dispose the notification thread. 11/04/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.sync.shutdown(this);
		
		// Clear the queue to release resources. 11/04/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.sync.shutdown(this);
		
		// Clear the queue to release resources. 11/04/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	/*
	 * Expose the key for the convenient management. 11/04/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	public boolean isHung()
	{
		return this.isHung.get();
	}

	/*
	 * Enqueue a notification into the thread. 11/04/2014, Bing Li
	 */
	public void enqueue(Notification notification) throws IllegalStateException
	{
		// Set the state of the thread to be busy. 11/04/2014, Bing Li
		this.idleLock.lock();
		try
		{
			// Set the state of busy for the thread. 02/07/2016, Bing Li
			this.isIdle = false;
			// Enqueue the notification. 11/04/2014, Bing Li
			this.queue.add(notification);
		}
		finally
		{
			this.idleLock.unlock();
		}
		// Notify the waiting thread to keep on working since new notifications are received. 09/22/2014, Bing Li
		this.sync.signal();
	}
	
	/*
	 * Get the current size of the queue. 11/04/2014, Bing Li
	 */
	@Override
	public int getWorkload()
	{
		return this.queue.size();
	}

	/*
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/04/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time, which is determined by the value of waitTime. 11/04/2014, Bing Li
		if (this.sync.holdOn(waitTime))
		{
			
			this.idleLock.lock();
			// Only when the queue is empty, the thread is set to be busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
				// Set the state of the thread to be idle after waiting for some time. 11/04/2014, Bing Li
				this.isIdle = true;
			}
			this.idleLock.unlock();
		}
	}
	
	/*
	 * The method intends to stop the thread temporarily until a signal wakes it up. 11/04/2014, Bing Li
	 */
	public void holdOn()
	{
		// Wait for some time, which is determined by the value of waitTime. 11/04/2014, Bing Li
		this.sync.holdOn();
		
		this.idleLock.lock();
		// Only when the queue is empty, the thread is set to be busy. 02/07/2016, Bing Li
		if (this.queue.size() <= 0)
		{
			// Set the state of the thread to be idle after waiting for some time. 11/04/2014, Bing Li
			this.isIdle = true;
		}
		this.idleLock.unlock();
	}

	/*
	 * The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
	 * 
	 * Wake up the thread to keep working. It is usually invoked by programmers rather than the thread pool, NotificationDispatcher. 09/11/2019, Bing Li
	 */
	/*
	public void signal()
	{
		this.sync.signal();
	}
	*/
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/04/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.sync.isShutdown();
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/04/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.taskSize;
	}
	
	/*
	 * Expose the task size. 05/19/2018, Bing Li
	 */
	public int getTaskSize()
	{
		return this.taskSize;
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/04/2014, Bing Li 
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/04/2014, Bing Li
	 */
	public boolean isIdle()
	{
		this.idleLock.lock();
		try
		{
			// The thread is believed to be idle only when the notification queue is empty and the idle is set to be true. The lock mechanism prevents one possibility that the queue gets new messages and the idle is set to be true. The situation occurs when the size of the queue and the idle value are checked asynchronously. Both of them being detected are a better solution. The idle guarantees the sufficient time has been waited and the queue size indicates that the thread is really not busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
				return this.isIdle;
			}
			else
			{
				// If the queue size is not empty, the thread is believed to be busy even though the idle is set to be true. 02/07/2016, Bing Li
				return false;
			}
		}
		finally
		{
			this.idleLock.unlock();
		}
	}
	
	/*
	 * Dequeue the notification from the queue. 11/04/2014, Bing Li
	 */
	public Notification getNotification() throws InterruptedException
	{
		this.isHung.set(true);
		return this.queue.poll();
	}

	/*
	 * Get the notification but leave the notification in the queue. 11/04/2014, Bing Li
	 */
	public Notification peekNotification()
	{
		this.isHung.set(true);
		return this.queue.peek();
	}

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 * 
	public boolean isWaitForEver()
	{
		return this.isWaitForEver.get();
	}
	
	public void setWaitForEver()
	{
		this.waitTime.set(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
		this.isWaitForEver.set(true);
	}
	
	public long getWaitTime()
	{
		return this.waitTime.get();
	}
	
	public void setWait(long time)
	{
		this.waitTime.set(time);
		this.isWaitForEver.set(false);
	}
	
	public void resetWait()
	{
		this.isWaitForEver.set(false);
		this.waitTime.set(0);
	}
	*/
	
	/*
	 * Dispose the notification. 09/22/2014, Bing Li
	 */
	public synchronized void disposeMessage(Notification notification)
	{
		this.isHung.set(false);
		notification = null;
	}

	/*
	 * Dispose something that generates during the procedure handling the notification. Usually, it is called during a multicasting procedure. 07/23/2017, Bing Li
	 */
	public synchronized void dispose(Object obj)
	{
		this.isHung.set(false);
		obj = null;
	}
}
