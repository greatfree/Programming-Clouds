package com.greatfree.concurrency;

import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.util.Tools;
import com.greatfree.util.UtilConfig;

/*
 * A fundamental thread that receives and processes notifications as an object concurrently. Notifications are put into a queue and prepare for further processing. It must be derived by sub classes to process specific notifications. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class NotificationObjectQueue<Notification> extends Thread
{
	// Declare the key for the notification thread. 11/20/2014, Bing Li
	private String key;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/20/2014, Bing Li
	private LinkedBlockingQueue<Notification> queue;
	// Declare the size of the queue. 11/20/2014, Bing Li
	private int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/20/2014, Bing Li
	private Collaborator collaborator;
	// The flag that represents the busy/idle state of the thread. 11/20/2014, Bing Li
	private boolean isIdle;
	
	/*
	 * Initialize the notification thread. This constructor has no limit on the size of the queue. 11/20/2014, Bing Li
	 */
	public NotificationObjectQueue()
	{
		// Generate the key. 11/20/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue without any size constraint. 11/20/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Notification>();
		// Ignore the value of taskSize. 11/20/2014, Bing Li
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		// Initialize the collaborator. 11/20/2014, Bing Li
		this.collaborator = new Collaborator();
		// Set the idle state to false. 11/20/2014, Bing Li
		this.isIdle = false;
	}
	
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
		this.collaborator = new Collaborator();
		// Set the idle state to false. 11/20/2014, Bing Li
		this.isIdle = false;
	}
	
	/*
	 * Dispose the notification thread. 11/20/2014, Bing Li
	 */
	public synchronized void dispose()
	{
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
		// Clear the queue to release resources. 11/20/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
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
	 * Enqueue a notification into the thread. 11/20/2014, Bing Li
	 */
	public void enqueue(Notification notification)
	{
		// Set the state of the thread to be busy. 11/20/2014, Bing Li
		this.setBusy();
		// Enqueue the notification. 11/20/2014, Bing Li
		this.queue.add(notification);
		// Notify the waiting thread to keep on working since new notifications are received. 11/20/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * Set the state to be busy. 11/20/2014, Bing Li
	 */
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	
	/*
	 * Set the state to be idle. 11/20/2014, Bing Li
	 */
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	
	/*
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/20/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Wait for some time, which is determined by the value of waitTime. 11/20/2014, Bing Li
		this.collaborator.holdOn(waitTime);
		// Set the state of the thread to be idle after waiting for some time. 11/20/2014, Bing Li
		this.setIdle();
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
	public synchronized boolean isIdle()
	{
		return this.isIdle;
	}
	
	/*
	 * Get the current size of the queue. 11/20/2014, Bing Li
	 */
	public int getQueueSize()
	{
		return this.queue.size();
	}
	
	/*
	 * Get the notification but leave the notification in the queue. 11/20/2014, Bing Li
	 */
	public Notification peekNotification()
	{
		return this.queue.peek();
	}
	
	/*
	 * Dequeue the notification stream from the queue. 11/20/2014, Bing Li
	 */
	public Notification getNotification() throws InterruptedException
	{
		return this.queue.take();
	}
	
	/*
	 * Dispose the notification. 11/20/2014, Bing Li
	 */
	public synchronized void disposeObject(Notification notification)
	{
		notification = null;
	}
}
