package org.greatfree.concurrency.reactive;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.MessageBindable;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The thread is different from NotificationQueue in the sense that it deals with the case when a notification is shared by multiple threads rather than just one. Therefore, it is necessary to implement a synchronization mechanism among those threads. 11/26/2014, Bing Li
 * 
 * For example, if no synchronization, it is possible that a message is disposed while it is consumed in another one. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
// public abstract class BoundNotificationQueue<Notification extends ServerMessage, Binder extends MessageBindable<Notification>> implements Runnable, Comparable<BoundNotificationQueue<Notification, Binder>>
public abstract class BoundNotificationQueue<Notification extends ServerMessage, Binder extends MessageBindable<Notification>> extends RunnerTask
{
	// Declare the key for the notification thread. 11/26/2014, Bing Li
	private final String key;
	// The thread is managed by a dispatcher. The key represents the dispatcher. 11/26/2014, Bing Li
	private String dispatcherKey;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/26/2014, Bing Li
	private Queue<Notification> queue;
	// Declare the size of the queue. 11/26/2014, Bing Li
	private final int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/26/2014, Bing Li
	private Sync collaborator;
	// The flag that indicates the busy/idle state of the thread. 11/26/2014, Bing Li
	private boolean isIdle;
	// The binder that synchronizes the threads that share the notification. 11/26/2014, Bing Li
	private Binder binder;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;

	/*
	 * Initialize the bound notification thread. This constructor has no limit on the size of the queue. It is required to input the binder and the dispatcher key. 11/26/2014, Bing Li
	 */
	public BoundNotificationQueue(Binder binder, String dispatcherKey)
	{
		// Generate the key. 11/26/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		this.dispatcherKey = dispatcherKey;
		this.queue = new LinkedBlockingQueue<Notification>();
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		this.collaborator = new Sync();
		this.isIdle = false;
		this.binder = binder;
		this.idleLock = new ReentrantLock();
	}
	
	/*
	 * Initialize the bound notification thread. This constructor has a limit on the size of the queue. It is required to input the binder and the dispatcher key. 11/26/2014, Bing Li
	 */
	public BoundNotificationQueue(int taskSize, String dispatcherKey, Binder binder)
	{
		this.key = Tools.generateUniqueKey();
		this.dispatcherKey = dispatcherKey;
		this.queue = new LinkedBlockingQueue<Notification>();
		this.taskSize = taskSize;
		this.collaborator = new Sync();
		this.isIdle = false;
		this.binder = binder;
		this.idleLock = new ReentrantLock();
	}
	
	/*
	 * Dispose the bound notification thread. 11/26/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		/*
		// Set the flag to be the state of being shutdown. 11/26/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the thread being waiting to go forward. Since the shutdown flag is set, the thread must die for the notification. 11/26/2014, Bing Li
		this.collaborator.signalAll();
		try
		{
			// Wait for the thread to die. 11/26/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/
		
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/26/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/26/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
	}

	/*
	 * Expose the key for the convenient management. 11/26/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the dispatcher key. 11/26/2014, Bing Li
	 */
	public String getDispatcherKey()
	{
		return this.dispatcherKey;
	}
	
	/*
	 * Enqueue a notification into the thread. 11/26/2014, Bing Li
	 */
	public void enqueue(Notification notification) throws IllegalStateException
	{
		// Set the state of the thread to be busy. 11/26/2014, Bing Li
//		this.setBusy();
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		// Enqueue the notification. 11/26/2014, Bing Li
		this.queue.add(notification);
		this.idleLock.unlock();
		// Notify the waiting thread to keep on working since new notifications are received. 11/26/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/26/2014, Bing Li
	 */
	/*
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	*/
	
	/*
	 * Set the state to be idle. 11/26/2014, Bing Li
	 */
	/*
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	*/
	
	/*
	 * Get the current size of the queue. 11/26/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
	{
		return this.queue.size();
	}

	/*
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/26/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// The lock intends to avoid the problem to shutdown the thread when the thread is holding on. 02/06/2016, Bing Li
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		this.idleLock.unlock();
		// Wait for some time, which is determined by the value of waitTime. 11/26/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 11/26/2014, Bing Li
//		this.setIdle();
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
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/26/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/26/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.taskSize;
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/26/2014, Bing Li 
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/26/2014, Bing Li
	 */
//	public synchronized boolean isIdle()
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
	 * Dequeue the notification from the queue. 11/26/2014, Bing Li
	 */
	public Notification getNotification() throws InterruptedException
	{
		return this.queue.poll();
	}
	
	/*
	 * Get the notification but leave the notification in the queue. 11/26/2014, Bing Li
	 */
	public Notification peekNotification()
	{
		return this.queue.peek();
	}

	/*
	 * Notify the bound notification is consumed by the thread, which is represented by the key. The binder then determines whether the behaviors that affect them can be executed. 11/26/2014, Bing Li
	 */
	public synchronized void bind(String threadKey, Notification notification)
	{
		this.binder.bind(threadKey, notification);
	}

	/*
	 * Compare threads according to their task loads in the notification queue. 02/01/2016, Bing Li
	 */
	/*
	@Override
	public int compareTo(BoundNotificationQueue<Notification, Binder> obj)
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
	*/
}
