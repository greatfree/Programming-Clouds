package org.greatfree.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The class an integrated one that helps developers to control concurrency. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class Sync
{
	// A unique key of the class. 07/30/2014, Bing Li
	private String key;
	// The waitLock is the design of JDK Lock. It is used to achieve the goal of
	// concurrency control. 07/30/2014, Bing Li
	private Lock waitLock;
	// The waitCondition is the design of JDK Condition. It is used to achieve
	// the goal of concurrency control. 07/30/2014, Bing Li
	private Condition waitCondition;
	// The flag of shutdown. 07/30/2014, Bing Li
	private boolean isShutdown;
	// The flag of waiting. 07/30/2014, Bing Li
	private boolean isPaused;
	// The flag indicates whether a thread is running. 01/13/2016, Bing Li
//	private boolean isRunning;
	// CD represents the concurrency degree, which is used for map/reduce. Until the number of responses reaches the number of CD, it allows to signal the waiting thread to finish the reducing. 10/04/2019, Bing Li
	private int cd;

	/*
	 * Initialize. A collaborator usually works with a concurrent thread. It is possible the thread is alive, i.e., isShutdown is false, when when the collaborator is initialized. If so, the constructor is chosen. 02/01/2016, Bing Li
	 */
	public Sync()
	{
		// The key is assigned to a unique String. 07/30/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		this.waitLock = new ReentrantLock();
		this.waitCondition = this.waitLock.newCondition();
		this.isShutdown = false;
		this.isPaused = false;
//		this.isRunning = false;
		// When CD is equal to NO_COUNT, it indicates that it does not need to take into any map/reduce issues. The signaling can be performed immediately even when only unique corresponding event occurs. 10/04/2019, Bing Li
		this.cd = UtilConfig.NO_COUNT;
	}

	public Sync(int cd)
	{
		// The key is assigned to a unique String. 07/30/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		this.waitLock = new ReentrantLock();
		this.waitCondition = this.waitLock.newCondition();
		this.isShutdown = false;
		this.isPaused = false;
//		this.isRunning = false;
		// When CD is NOT equal to NO_COUNT, it indicates that it needs to take into any map/reduce issues. The signaling can be performed immediately until the number of corresponding events reach the value of CD. 10/04/2019, Bing Li
		this.cd = cd;
	}
	
	/*
	 * Initialize. A collaborator usually works with a concurrent thread. It is possible whether the thread is dead or alive is not determined when when the collaborator is initialized. If so, the constructor is chosen. 02/01/2016, Bing Li
	 */
	public Sync(boolean isShutdown)
	{
		this.key = Tools.generateUniqueKey();
		this.waitLock = new ReentrantLock();
		this.waitCondition = this.waitLock.newCondition();
		this.isShutdown = isShutdown;
		this.isPaused = false;
		this.cd = UtilConfig.NO_COUNT;
	}

	public Sync(boolean isShutdown, int cd)
	{
		this.key = Tools.generateUniqueKey();
		this.waitLock = new ReentrantLock();
		this.waitCondition = this.waitLock.newCondition();
		this.isShutdown = isShutdown;
		this.isPaused = false;
		this.cd = cd;
	}

	/*
	 * The class is used to detect whether a managed thread is shutdown.
	 * 07/30/2014, Bing Li
	 */
//	public synchronized boolean isShutdown()
	public boolean isShutdown()
	{
		this.waitLock.lock();
		try
		{
//			this.isRunning = true;
			return this.isShutdown;
		}
		finally
		{
			this.waitLock.unlock();
		}
	}

	/*
	 * The class sets the status of a managed thread to shut down. The thread is
	 * shut down when the current task is finished. 07/30/2014, Bing Li
	 */
//	public synchronized void setShutdown()
	/*
	public void setShutdown()
	{
		this.waitLock.lock();
		try
		{
//			this.isRunning = false;
			this.isShutdown = true;
		}
		finally
		{
			this.waitLock.unlock();
		}
	}
	*/

	/*
	 * Shutdown the current thread. This is usually invoked by a thread that works itself without any other invoking any other threads. 02/26/2016, Bing Li
	 */
	public void shutdown(Runnable t) throws InterruptedException
	{
		this.waitLock.lock();
		try
		{
			this.isShutdown = true;
			this.waitCondition.signalAll();
//			t.join();
		}
		finally
		{
			this.waitLock.unlock();
		}
	}

	/*
	 * Shutdown the current thread. This is usually invoked by a thread management mechanism, such as a pool. 02/26/2016, Bing Li
	 */
	public void shutdown()
	{
		this.waitLock.lock();
		try
		{
			this.isShutdown = true;
			this.waitCondition.signalAll();
		}
		finally
		{
			this.waitLock.unlock();
		}
	}

	/*
	 * To reuse a thread, it is required to reset the shutdown flag by the
	 * method. 07/30/2014, Bing Li
	 */
//	public synchronized void reset()
	public void reset()
	{
		this.waitLock.lock();
		try
		{
			this.isPaused = false;
			this.isShutdown = false;
		}
		finally
		{
			this.waitLock.unlock();
		}
	}

	/*
	 * Detect whether the thread the current collaborator resides in is running or not. 01/13/2016, Bing Li
	 */
	/*
	public synchronized boolean isRunning()
	{
		return this.isRunning;
	}
	*/

	/*
	 * The method notifies a thread to acquire the lock to keep going.
	 * 07/30/2014, Bing Li
	 */
	public void signal()
	{
		this.waitLock.lock();
		this.waitCondition.signal();
		this.waitLock.unlock();
	}

	/*
	 * The method notifies all of the managed threads to acquire the lock to
	 * keep going. 07/30/2014, Bing Li
	 */
	public void signalAll()
	{
		this.waitLock.lock();
		this.waitCondition.signalAll();
		this.waitLock.unlock();
	}

	/*
	 * The method forces the managed thread to wait until it is notified that a
	 * lock is available. 07/30/2014, Bing Li
	 */
//	public void holdOn() throws InterruptedException
	public void holdOn()
	{
		// Check whether the flag of shutdown is set. The waiting is performed
		// only when the flag of shutdown is not set. 11/20/2014, Bing Li
//		if (!this.isShutdown())
//		{
		this.waitLock.lock();
		if (!this.isShutdown)
		{
			try
			{
				this.waitCondition.await();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		this.waitLock.unlock();
	}

	/*
	 * The method forces the managed thread to wait for some time in the unit of
	 * millisecond until it is notified that a lock is available. 07/30/2014,
	 * Bing Li
	 */
//	public void holdOn(long waitTime) throws InterruptedException
	public void holdOn(long waitTime)
	{
		// Check whether the flag of shutdown is set. The waiting is performed
		// only when the flag of shutdown is not set. 11/20/2014, Bing Li
//		if (!this.isShutdown())
//		{
		this.waitLock.lock();
		if (!this.isShutdown)
		{
			try
			{
				this.waitCondition.await(waitTime, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		this.waitLock.unlock();
	}

	/*
	 * The key is unique to the class. It is used to keep multiple Collaborators
	 * conveniently in a collection, such as HashMap. 07/30/2014, Bing Li
	 */
	public synchronized String getKey()
	{
		return this.key;
	}

	/*
	 * To generate a new key for the class. It is used scarcely. Sometimes when
	 * multicasting requests, the key needs to be reset to wait for
	 * corresponding responses, e.g., RootRequestBroadcastor. 07/30/2014, Bing
	 * Li
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
	 * Set the state of isPaused to false and notify the thread to acquire a
	 * lock to continue to work. 07/30/2014, Bing Li
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
	
	public int getCD()
	{
		return this.cd;
	}
}
