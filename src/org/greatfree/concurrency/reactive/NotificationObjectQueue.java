package org.greatfree.concurrency.reactive;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.util.Tools;

/*
 * A fundamental thread that receives and processes notifications as an object concurrently. Notifications are put into a queue and prepare for further processing. It must be derived by sub classes to process specific notifications. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
// public abstract class NotificationObjectQueue<Notification> implements Runnable, Comparable<NotificationObjectQueue<Notification>>
public abstract class NotificationObjectQueue<Notification> extends RunnerTask
{
	// Declare the key for the notification thread. 11/20/2014, Bing Li
	private final String key;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/20/2014, Bing Li
	private Queue<Notification> queue;
	// Declare the size of the queue. 11/20/2014, Bing Li
	private final int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/20/2014, Bing Li
	private Sync collaborator;
	// The flag that represents the busy/idle state of the thread. 11/20/2014, Bing Li
	private boolean isIdle;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;

	// The thread is possibly interrupted by the thread pool/the system when the thread is hung by a task permanently. If so, the exception is not required to be displayed according to the flag. 11/05/2019, Bing Li
	private AtomicBoolean isSysInterrupted;
	private AtomicBoolean isHung;

	/*
	 * Initialize the notification thread. This constructor has no limit on the size of the queue. 11/20/2014, Bing Li
	 */
	/*
	public NotificationObjectQueue()
	{
		// Generate the key. 11/20/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue without any size constraint. 11/20/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Notification>();
		// Ignore the value of taskSize. 11/20/2014, Bing Li
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		// Initialize the collaborator. 11/20/2014, Bing Li
		this.collaborator = new Sync();
		// Set the idle state to false. 11/20/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
	}
	*/
	
	/*
	 * Initialize the notification thread. This constructor has a limit on the size of the queue. 11/20/2014, Bing Li
	 */
	public NotificationObjectQueue(int taskSize)
	{
		// Generate the key. 11/20/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue with the particular size constraint. 11/20/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Notification>();
		// Set the value of taskSize. 11/20/2014, Bing Li
		this.taskSize = taskSize;
		// Initialize the collaborator. 11/20/2014, Bing Li
		this.collaborator = new Sync();
		// Set the idle state to false. 11/20/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
		this.isSysInterrupted = new AtomicBoolean(false);
		this.isHung = new AtomicBoolean(false);
	}
	
	/*
	 * Dispose the notification thread. 11/20/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		/*
		// Set the flag to be the state of being shutdown. 11/20/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the thread being waiting to go forward. Since the shutdown flag is set, the thread must die for the notification. 11/20/2014, Bing Li
		this.collaborator.signalAll();
		try
		{
			// Wait for the thread to die. 11/20/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/20/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		if (this.isHung.get())
		{
			this.interrupt();
		}
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/20/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		if (this.isHung.get())
		{
			this.interrupt();
		}
	}

	/*
	 * Expose the key for the convenient management. 11/20/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * The method aims to kill one thread that is hung permanently by a task. 11/05/2019, Bing Li
	 */
	public void interrupt()
	{
		this.isSysInterrupted.set(true);
		Thread.currentThread().interrupt();
	}
	
	public boolean isSysInterrupted()
	{
		return this.isSysInterrupted.get();
	}

	public boolean isHung()
	{
		return this.isHung.get();
	}
	
	/*
	 * Enqueue a notification into the thread. 11/20/2014, Bing Li
	 */
	public void enqueue(Notification notification)
	{
		// Set the state of the thread to be busy. 11/20/2014, Bing Li
//			this.setBusy();
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		// Enqueue the notification. 11/20/2014, Bing Li
		this.queue.add(notification);
		this.idleLock.unlock();
		// Notify the waiting thread to keep on working since new notifications are received. 11/20/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/20/2014, Bing Li
	 */
	/*
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	*/
	
	/*
	 * Set the state to be idle. 11/20/2014, Bing Li
	 */
	/*
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	*/
	
	/*
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/20/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// The lock intends to avoid the problem to shutdown the thread when the thread is holding on. 02/06/2016, Bing Li
		/*
		this.idleLock.lock();
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		this.idleLock.unlock();
		*/
		// Wait for some time, which is determined by the value of waitTime. 11/20/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 11/20/2014, Bing Li
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
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/20/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/20/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.queue.size() >= this.taskSize;
	}

	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/20/2014, Bing Li 
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/20/2014, Bing Li
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
//				System.out.println("1) NotificationObjectQueue-isIdle(): " + this.hashCode() + ": queue size = " + this.queue.size() + ", isIdle = " + this.isIdle);
				return this.isIdle;
			}
			else
			{
				// If the queue size is not empty, the thread is believed to be busy even though the idle is set to be true. 02/07/2016, Bing Li
//				System.out.println("2) NotificationObjectQueue-isIdle(): " + this.hashCode() + ": queue size = " + this.queue.size() + ", isIdle = " + this.isIdle);
				return false;
			}
		}
		finally
		{
			this.idleLock.unlock();
		}
	}
	
	/*
	 * Get the current size of the queue. 11/20/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
	{
		return this.queue.size();
	}
	
	/*
	 * Get the notification but leave the notification in the queue. 11/20/2014, Bing Li
	 */
	public Notification peekNotification()
	{
		this.isHung.set(true);
		return this.queue.peek();
	}
	
	/*
	 * Dequeue the notification stream from the queue. 11/20/2014, Bing Li
	 */
	public Notification getNotification() throws InterruptedException
	{
		this.isHung.set(true);
		return this.queue.poll();
	}
	
	/*
	 * Dispose the notification. 11/20/2014, Bing Li
	 */
	public synchronized void disposeObject(Notification notification)
	{
		this.isHung.set(false);
		notification = null;
	}

	/*
	 * Compare threads according to their task loads in the notification queue. 02/01/2016, Bing Li
	 */
	/*
	@Override
	public int compareTo(NotificationObjectQueue<Notification> obj)
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
