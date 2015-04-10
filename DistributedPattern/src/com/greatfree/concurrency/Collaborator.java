package com.greatfree.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.greatfree.util.Tools;

/*
 * The class an integrated one that helps developers to control concurrency. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class Collaborator
{
	// A unique key of the class. 07/30/2014, Bing Li
	private String key;
	// The waitLock is the design of JDK Lock. It is used to achieve the goal of concurrency control. 07/30/2014, Bing Li
	private Lock waitLock;
	// The waitCondition is the design of JDK Condition. It is used to achieve the goal of concurrency control. 07/30/2014, Bing Li
	private Condition waitCondition;
	// The flag of shutdown. 07/30/2014, Bing Li
	private boolean isShutdown;
	// The flag of waiting. 07/30/2014, Bing Li
	private boolean isPaused;
	
	public Collaborator()
	{
		// The key is assigned to a unique String. 07/30/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		this.waitLock = new ReentrantLock();
		this.waitCondition = this.waitLock.newCondition();
		this.isShutdown = false;
		this.isPaused = false;
	}

	/*
	 * The class is used to detect whether a managed thread is shutdown. 07/30/2014, Bing Li
	 */
	public synchronized boolean isShutdown()
	{
		return this.isShutdown;
	}

	/*
	 * The class sets the status of a managed thread to shut down. The thread is shut down when the current task is finished. 07/30/2014, Bing Li
	 */
	public synchronized void setShutdown()
	{
		this.isShutdown = true;
	}

	/*
	 * To reuse a thread, it is required to reset the shutdown flag by the method. 07/30/2014, Bing Li
	 */
	public synchronized void reset()
	{
		this.isShutdown = false;
	}

	/*
	 * The method notifies a thread to acquire the lock to keep going. 07/30/2014, Bing Li
	 */
	public void signal()
	{
		this.waitLock.lock();
		this.waitCondition.signal();
		this.waitLock.unlock();
	}

	/*
	 * The method notifies all of the managed threads to acquire the lock to keep going. 07/30/2014, Bing Li
	 */
	public void signalAll()
	{
		this.waitLock.lock();
		this.waitCondition.signalAll();
		this.waitLock.unlock();
	}

	/*
	 * The method forces the managed thread to wait until it is notified that a lock is available. 07/30/2014, Bing Li
	 */
	public void holdOn() throws InterruptedException
	{
		// Check whether the flag of shutdown is set. The waiting is performed only when the flag of shutdown is not set. 11/20/2014, Bing Li
		if (!this.isShutdown())
		{
			this.waitLock.lock();
			this.waitCondition.await();
			this.waitLock.unlock();
		}
	}
	
	/*
	 * The method forces the managed thread to wait for some time in the unit of millisecond until it is notified that a lock is available. 07/30/2014, Bing Li
	 */
	public void holdOn(long waitTime) throws InterruptedException
	{
		// Check whether the flag of shutdown is set. The waiting is performed only when the flag of shutdown is not set. 11/20/2014, Bing Li
		if (!this.isShutdown())
		{
			this.waitLock.lock();
			this.waitCondition.await(waitTime, TimeUnit.MILLISECONDS);
			this.waitLock.unlock();
		}
	}

	/*
	 * The key is unique to the class. It is used to keep multiple Collaborators conveniently in a collection, such as HashMap. 07/30/2014, Bing Li
	 */
	public synchronized String getKey()
	{
		return this.key;
	}

	/*
	 * To generate a new key for the class. It is used scarcely. Sometimes when multicasting requests, the key needs to be reset to wait for corresponding responses, e.g., RootRequestBroadcastor. 07/30/2014, Bing Li
	 */
	public synchronized String resetKey()
	{
		this.key = Tools.generateUniqueKey();
		return this.key;
	}

	/*
	 * Set the state of isPause to true. 07/30/2014, Bing Li
	 */
	public synchronized void setPause()
	{
		this.isPaused = true;
	}

	/*
	 * Set the state of isPaused to false and notify the thread to acquire a lock to continue to work. 07/30/2014, Bing Li 
	 */
	public void keepOn()
	{
		this.waitLock.lock();
		this.isPaused = false;
		this.waitCondition.signal();
		this.waitLock.unlock();
	}

	/*
	 * Check the state of isPaused. 07/30/2014, Bing Li
	 */
	public synchronized boolean isPaused()
	{
		return this.isPaused;
	}
}
