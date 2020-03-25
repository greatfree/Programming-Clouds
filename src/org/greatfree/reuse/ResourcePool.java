package org.greatfree.reuse;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.Sync;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/*
 * The pool aims to manage resources that are scheduled by their idle lengths. The one that is idle longer has the higher probability to be reused than the one that is idle shorter. Different from QueuedPool, this pool does not care about the type of resources. It assumes that all of resources in the pool are classified in the same type. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class ResourcePool<Source, Resource extends HashFreeObject, Creator extends HashCreatable<Source, Resource>, Disposer extends HashDisposable<Resource>>
{
	// The map keeps all of the busy resources by their hash key. 11/26/2014, Bing Li
	private Map<String, Resource> busyMap;
	// The map keeps all of the idle resources in the same queue. Since usually they are the same type, it is unnecessary to differ them by their respective queues. 11/26/2014, Bing Li
	private Queue<Resource> idleQueue;
	// The lock is used to reach the goal of thread-safety. 11/26/2014, Bing Li
	private ReentrantLock rscLock;
	// The timer to be used to manage the period and schedule the task to check periodically whether idle resources are idle long enough. 11/26/2014, Bing Li
	private Timer checkTimer;
	// The instance of IdleChecker invokes the method of checkIdle() periodically and concurrently. 11/26/2014, Bing Li
	private IdleChecker<Source, Resource, Creator, Disposer> idleChecker;
	// Declare the maximum idle time length. 11/26/2014, Bing Li
	private long maxIdleTime;
	// Declare the size of the pool. 11/26/2014, Bing Li
	private int poolSize;
	// The creator is used to create the instance of the resources managed by the pool. 11/26/2014, Bing Li
	private Creator creator;
	// The disposer is used to dispose the instance of the resources managed by the pool. 11/26/2014, Bing Li
	private Disposer disposer;
	// The collaborator is used to implement the mechanism of notify/wait to coordinate resource management for multiple threads. 11/26/2014, Bing Li
	private Sync collaborator;
	// When resources are not available, it is required to wait for some time. This is the time length to wait. 11/26/2014, Bing Li
	private long waitTime;

	/*
	 * Initialize. 11/26/2014, Bing Li
	 */
	public ResourcePool(int poolSize, Creator creator, Disposer disposer, long waitTime)
	{
		this.busyMap = new ConcurrentHashMap<String, Resource>();
		this.idleQueue = new LinkedBlockingQueue<Resource>();
		this.rscLock = new ReentrantLock();

		this.poolSize = poolSize;
		this.creator = creator;
		this.checkTimer = UtilConfig.NO_TIMER;
		this.disposer = disposer;
		this.collaborator = new Sync();
		this.waitTime = waitTime;
	}
	
	/*
	 * Shutdown the pool. 11/26/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		/*
		// Set the shutdown flag to be true. Thus, the loop in the method of get() can be terminated. 11/26/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the method of get() to terminate and all of the threads that are waiting for the resources are unblocked. 11/26/2014, Bing Li 
		this.collaborator.signalAll();
		*/
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Dispose all of the busy resources. It might lose data if no relevant management approaches are adopted. 11/26/2014, Bing Li
		for (Resource t : this.busyMap.values())
		{
			this.disposer.dispose(t);
		}
		this.busyMap.clear();

		// Dispose all of the idle resources. It might lose data if no relevant management approaches are adopted. 11/26/2014, Bing Li
		Resource t;
		while (this.idleQueue.size() > 0)
		{
			t = this.idleQueue.poll();
			this.disposer.dispose(t);
		}
		this.idleQueue.clear();

		// Terminate the idle state checker that periodically runs. For it is possible that the checker is not initialized in some cases, it needs to check whether it is null or not. 11/26/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Terminate the timer that manages the period to run the idle state checker. For it is possible that the timer is not initialized in some cases, it needs to check whether it is null or not. 11/26/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
	}

	/*
	 * Initialize the idle state checker and the timer to manage idle resources periodically when needed. This method is not always indispensable. 11/26/2014, Bing Li
	 */
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		// Initialize the timer. 11/26/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the checker. 11/26/2014, Bing Li
		this.idleChecker = new IdleChecker<Source, Resource, Creator, Disposer>(this);
		// Schedule the task of checking idle states of resources. 11/26/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
		// Set the value of maximum idle time. 11/26/2014, Bing Li
		this.maxIdleTime = maxIdleTime;
	}
	
	/*
	 * Check the idle resources. The method is called back by the IdleChecker. 11/26/2014, Bing Li
	 */
	public void checkIdle() throws InterruptedException
	{
		Resource t;
		// Get the current time which is used to calculate the idle time length. 11/26/2014, Bing Li
		Date currentTime = Calendar.getInstance().getTime();
		this.rscLock.lock();
		// Scan all of the idle resources. 11/26/2014, Bing Li
		while (this.idleQueue.size() > 0)
		{
			// Peek the first resource. If it is not idle long enough, the checking can be returned because the first one must be idle for the most long time. 11/26/2014, Bing Li
			t = this.idleQueue.peek();
			// Estimate the idle time and check whether it is long enough. 11/26/2014, Bing Li
			if (Time.getTimespanInMilliSecond(currentTime, t.getAccessedTime()) > this.maxIdleTime)
			{
				// If it is idle long enough, it is dequeued from the queue. 11/26/2014, Bing Li
				t = this.idleQueue.poll();
				// Dispose the resource. 11/26/2014, Bing Li
				this.disposer.dispose(t);
			}
			else
			{
				// Since the first resource of the queue is the one that is idle longer than any others in the queue, it denotes that all of the resources in the queue are not idle longer than the upper limit. 11/26/2014, Bing Li
				break;
			}
		}
		this.rscLock.unlock();
		// Notify the blocked thread that the size of the assigned resources is lowered such that it is time for it to get a resource it needs. A bunch of resources might be disposed during the procedure. It is reasonable to signal all rather than signal a single waiting thread. 11/26/2014, Bing Li 
		this.collaborator.signalAll();
	}
	
	/*
	 * Dispose a resource explicitly when it is not needed. Usually, it is not invoked by the pool but by the threads that consume the resource. It happens when it is confirmed that the resource is never needed in a specific case. 11/26/2014, Bing Li
	 */
	public void dispose(Resource t) throws InterruptedException
	{
		// Check whether the resource is valid. 11/26/2014, Bing Li
		if (t != null)
		{
			this.rscLock.lock();
			// Check whether the busy collection contains the resource to be disposed. 11/26/2014, Bing Li
			if (this.busyMap.containsKey(t.getHashKey()))
			{
				// If the resource exists in the collection, remove the resource from the collection. 11/26/2014, Bing Li
				this.busyMap.remove(t.getHashKey());
			}
			// Dispose the resource. 11/26/2014, Bing Li
			this.disposer.dispose(t);
			this.rscLock.unlock();
			// Notify the thread that is blocked for the maximum size of the pool is reached. 11/26/2014, Bing Li
			this.collaborator.signal();
		}
	}
	
	/*
	 * Check whether a specific resource is busy. 11/26/2014, Bing Li
	 */
	public boolean isBusy(String resourceKey)
	{
		this.rscLock.lock();
		try
		{
			return this.busyMap.containsKey(resourceKey);
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Collect a resource. When the resource finishes its task, the method is invoked by the corresponding thread such that the resource can be reused. 11/26/2014, Bing Li
	 */
	public void collect(Resource t)
	{
		// Check whether the resource is valid. 11/26/2014, Bing Li
		if (t != null)
		{
			this.rscLock.lock();
			// Check whether the resource is contained in the busy collection. 11/26/2014, Bing Li
			if (this.busyMap.containsKey(t.getHashKey()))
			{
				// Remove it from the busy collection. 11/26/2014, Bing Li
				this.busyMap.remove(t.getHashKey());
			}
			// Set the accessed time stamp for the resource. The time stamp is the idle starting moment of the resource. It is used to calculate whether the resource is idle for long enough. 11/26/2014, Bing Li
			t.setAccessedTime();
			// Enqueue the idle resource into the idle queue. 11/26/2014, Bing Li
			this.idleQueue.add(t);
			this.rscLock.unlock();
			// Notify the thread that is blocked for the maximum size of the pool is reached. 11/26/2014, Bing Li
			this.collaborator.signal();
		}
	}

	/*
	 * Expose all the hash keys of the busy resources. 11/26/2014, Bing Li
	 */
	public Set<String> getBusyKeys()
	{
		this.rscLock.lock();
		try
		{
			return this.busyMap.keySet();
		}
		finally
		{
			this.rscLock.unlock();
		}
	}

	/*
	 * Get the busy resource from the busy collection. 11/26/2014, Bing Li
	 */
	public Resource getBusyResource(String key)
	{
		this.rscLock.lock();
		try
		{
			if (this.busyMap.containsKey(key))
			{
				return this.busyMap.get(key);
			}
			return null;
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Get the resource by its source, which contains the arguments to create the resource. 11/26/2014, Bing Li
	 */
	public Resource get(Source source) throws InstantiationException, IllegalAccessException
	{
		Resource rsc = null;
		// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 11/26/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			this.rscLock.lock();
			try
			{
				// Check whether the idle queue is empty. 11/26/2014, Bing Li
				if (!this.idleQueue.isEmpty())
				{
					// Dequeue the idle resource. 11/26/2014, Bing Li
					rsc = this.idleQueue.poll();
					// Check whether the resource is valid. 11/26/2014, Bing Li
					if (rsc != null)
					{
						// Set the busy starting time stamp. 11/26/2014, Bing Li
						rsc.setAccessedTime();
						// Put it into the busy map. 11/26/2014, Bing Li
						this.busyMap.put(rsc.getHashKey(), rsc);
					}
				}
				
				// Check whether the resource is valid. 11/26/2014, Bing Li
				if (rsc == null)
				{
					/*
					 * If the resource is invalid, it is necessary to create a new instance of the resource. 11/26/2014, Bing Li
					 */
					
					// Check whether the count of the total existing resources exceeds the upper limit. 11/26/2014, Bing Li 
					if (this.busyMap.size() + this.idleQueue.size() < this.poolSize)
					{
						// Create a new instance of the resource. 11/26/2014, Bing Li
						rsc = this.creator.createResourceInstance(source);
						// Put the new instance of resource into the busy collection. 11/26/2014, Bing Li
						this.busyMap.put(rsc.getHashKey(), rsc);
						// Return the new instance of the resource. 11/26/2014, Bing Li
						return rsc;
					}
				}
				else
				{
					// Return the instance of the idle resource. 11/26/2014, Bing Li
					return rsc;
				}
			}
			finally
			{
				this.rscLock.unlock();
			}

			/*
			 * If no such resources in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include resource disposals and idle resources being available. 11/26/2014, Bing Li
			 */

			// Check whether the pool is shutdown. 11/26/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 11/26/2014, Bing Li
				this.collaborator.holdOn(this.waitTime);
			}
		}
		return null;
	}
}
