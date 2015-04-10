package com.greatfree.concurrency;

import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.util.Tools;
import com.greatfree.util.UtilConfig;

/*
 * The thread is different from NotificationQueue in the sense that it deals with the case when a notification is shared by multiple threads rather than just one. Therefore, it is necessary to implement a synchronization mechanism among those threads. 11/26/2014, Bing Li
 * 
 * For example, if no synchronization, it is possible that a message is disposed while it is consumed in another one. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class BoundNotificationQueue<Notification extends ServerMessage, Binder extends MessageBindable<Notification>> extends Thread
{
	// Declare the key for the notification thread. 11/26/2014, Bing Li
	private String key;
	// The thread is managed by a dispatcher. The key represents the dispatcher. 11/26/2014, Bing Li
	private String dispatcherKey;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/26/2014, Bing Li
	private LinkedBlockingQueue<Notification> queue;
	// Declare the size of the queue. 11/26/2014, Bing Li
	private int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/26/2014, Bing Li
	private Collaborator collaborator;
	// The flag that indicates the busy/idle state of the thread. 11/26/2014, Bing Li
	private boolean isIdle;
	// The binder that synchronizes the threads that share the notification. 11/26/2014, Bing Li
	private Binder binder;

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
		this.collaborator = new Collaborator();
		this.isIdle = false;
		this.binder = binder;
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
		this.collaborator = new Collaborator();
		this.isIdle = false;
		this.binder = binder;
	}
	
	/*
	 * Dispose the bound notification thread. 11/26/2014, Bing Li
	 */
	public synchronized void dispose()
	{
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
		this.setBusy();
		// Enqueue the notification. 11/26/2014, Bing Li
		this.queue.add(notification);
		// Notify the waiting thread to keep on working since new notifications are received. 11/26/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/26/2014, Bing Li
	 */
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	
	/*
	 * Set the state to be idle. 11/26/2014, Bing Li
	 */
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	
	/*
	 * Get the current size of the queue. 11/26/2014, Bing Li
	 */
	public int getQueueSize()
	{
		return this.queue.size();
	}

	/*
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/26/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time, which is determined by the value of waitTime. 11/26/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 11/26/2014, Bing Li
		this.setIdle();
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
	public synchronized boolean isIdle()
	{
		return this.isIdle;
	}

	/*
	 * Dequeue the notification from the queue. 11/26/2014, Bing Li
	 */
	public Notification getNotification() throws InterruptedException
	{
		return this.queue.take();
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
}
