package org.greatfree.reuse;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.Sync;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The pool aims to manage resources that are scheduled by their idle lengths. The one that is idle longer has the higher probability to be reused than the one that is idle shorter. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class QueuedPool<Resource extends FreeObject, Creator extends Creatable<String, Resource>, Disposer extends Disposable<Resource>>
{
	// The map keeps all of the busy resources by their hash key. 11/03/2014, Bing Li
	private Map<String, Resource> busyMap;
	// The map keeps all of the idle resources in different queues by their type keys. Each type of idle resources has a dedicated queue. 11/03/2014, Bing Li
	private Map<String, Queue<Resource>> idleMap;
	// The lock is used to reach the goal of thread-safety. 11/03/2014, Bing Li
	private ReentrantLock rscLock;
	// The timer to be used to manage the period and schedule the task to check periodically whether idle resources are idle long enough. 11/03/2014, Bing Li
	private Timer checkTimer;
	// The instance of QueuedIdleChecker invokes the method of checkIdle() periodically and concurrently. 11/03/2014, Bing Li
	private QueuedIdleChecker<Resource, Creator, Disposer> idleChecker;
	// Declare the maximum idle time length. 11/03/2014, Bing Li
	private long maxIdleTime;
	// Declare the size of the pool. 11/03/2014, Bing Li
	private int poolSize;
	// The creator is used to create the instance of the resources managed by the pool. 11/03/2014, Bing Li
	private Creator creator;
	// The disposer is used to dispose the instance of the resources managed by the pool. 11/03/2014, Bing Li
	private Disposer disposer;
	// The collaborator is used to implement the mechanism of notify/wait to coordinate resource management for multiple threads. 11/03/2014, Bing Li
	private Sync collaborator;

	/*
	 * Initialize. 11/03/2014, Bing Li
	 */
	public QueuedPool(int poolSize, Creator creator, Disposer disposer)
	{
		this.busyMap = new HashMap<String, Resource>();
		this.idleMap = new HashMap<String, Queue<Resource>>();
		this.rscLock = new ReentrantLock();
		this.poolSize = poolSize;
		this.creator = creator;
		this.disposer = disposer;
		this.collaborator = new Sync();
	}

	/*
	 * Shutdown the pool. 11/03/2014, Bing Li
	 */
	public void shutdown() throws IOException
	{
		/*
		// Set the shutdown flag to be true. Thus, the loop in the method of get() can be terminated. 11/03/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the method of get() to terminate and all of the threads that are waiting for the resources are unblocked. 11/03/2014, Bing Li 
		this.collaborator.signalAll();
		*/
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Dispose all of the busy resources. It might lose data if no relevant management approaches are adopted. 11/03/2014, Bing Li
		for (Resource resource : this.busyMap.values())
		{
			this.disposer.dispose(resource);
		}
		this.busyMap.clear();
		
		// Dispose all of the idle resources. It might lose data if no relevant management approaches are adopted. 11/03/2014, Bing Li
		Set<String> tKeys = this.idleMap.keySet();
		Resource resource;
		for (String key : tKeys)
		{
			while (this.idleMap.get(key).size() > 0) 
			{
				resource = this.idleMap.get(key).poll();
				this.disposer.dispose(resource);
			}
		}
		this.idleMap.clear();

		// Terminate the idle state checker that periodically runs. For it is possible that the checker is not initialized in some cases, it needs to check whether it is null or not. 11/03/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Terminate the timer that manages the period to run the idle state checker. For it is possible that the timer is not initialized in some cases, it needs to check whether it is null or not. 11/03/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
	}
	
	/*
	 * Dispose a resource explicitly when it is not needed. Usually, it is not invoked by the pool but by the threads that consume the resource. It happens when it is confirmed that the resource is never needed in a specific case. 11/03/2014, Bing Li
	 */
	public void dispose(Resource rsc) throws IOException
	{
		this.rscLock.lock();
		// Check whether the resource is saved in the busy map. 11/03/2014, Bing Li
		if (this.busyMap.containsKey(rsc.getHashKey()))
		{
			// If the resource is in the busy map, remove it. 11/03/2014, Bing Li
			this.busyMap.remove(rsc.getHashKey());
			// Dispose the resource. 11/03/2014, Bing Li
			this.disposer.dispose(rsc);
		}
		else
		{
			// If the resource is not in the idle map, it is necessary to check whether it is saved in one of queues of idle map. 11/03/2014, Bing Li
			if (this.idleMap.containsKey(rsc.getObjectKey()))
			{
				// If the resource type is in one of queues of the idle map, declare an instance of resource. 11/03/2014, Bing Li
				Resource idleRsc;
				// Get the first key of the first resource in the queue. 11/03/2014, Bing Li
				String firstKey = this.idleMap.get(rsc.getObjectKey()).peek().getHashKey();
				// Check whether the key of the first resource of the queue is equal to the hash key of the resource to be disposed. 11/03/2014, Bing Li
				if (firstKey.equals(rsc.getHashKey()))
				{
					// If the above condition is fulfilled, it denotes that the resource is identical to the one to be disposed. Thus, remove it from the queue. 11/03/2014, Bing Li
					this.idleMap.get(rsc.getObjectKey()).poll();
					// Dispose the resource. 11/03/2014, Bing Li
					this.disposer.dispose(rsc);
				}
				else
				{
					// If the above condition is not fulfilled, it is necessary to check whether the resource is queued in the later position of the queue. 11/03/2014, Bing Li
					do
					{
						// Get the current first resource out of the queue. 11/03/2014, Bing Li
						idleRsc = this.idleMap.get(rsc.getObjectKey()).poll();
						// Check whether the resource removed from the queue is identical to the one to be disposed. 11/03/2014, Bing Li
						if (idleRsc.getHashKey().equals(rsc.getHashKey()))
						{
							// If the above condition is fulfilled, it denotes that the resource is identical to the one to be disposed. Thus, remove it from the queue. 11/03/2014, Bing Li
							this.disposer.dispose(rsc);
						}
						else
						{
							// If the above condition is not fulfilled, enqueue the resource just removed from the queue. 11/03/2014, Bing Li
							this.idleMap.get(rsc.getObjectKey()).add(idleRsc);
						}
					}
					// Check whether the resources in the queue are placed in the original position. If not, the loop continues. If so, the loop needs to quit. 11/03/2014, Bing Li
					while (!firstKey.equals(this.idleMap.get(rsc.getObjectKey()).peek().getHashKey()));
				}
			}
			else
			{
				// If resource to be removed is neither in the busy map nor the idle map, dispose it directly. 11/03/2014, Bing Li
				this.disposer.dispose(rsc);
			}
		}
		this.rscLock.unlock();
		// Notify the thread that is blocked for the maximum size of the pool is reached. 11/03/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * The method creates an instance without any management. That is used only when the pool is shutdown. It aims to avoid inconsistency when removing data by FileManager.RemoveFiles(). 11/03/2014, Bing Li
	 */
	public Resource create(String rscType) throws IOException, InterruptedException
	{
		return this.creator.createClientInstance(rscType);
	}
	
	/*
	 * Initialize the idle state checker and the timer to manage idle resources periodically when needed. This method is not always indispensable. 11/03/2014, Bing Li
	 */
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		// Initialize the timer. 11/03/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the checker. 11/03/2014, Bing Li
		this.idleChecker = new QueuedIdleChecker<Resource, Creator, Disposer>(this);
		// Schedule the task of checking idle states of resources. 11/03/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
		// Set the value of maximum idle time. 11/03/2014, Bing Li
		this.maxIdleTime = maxIdleTime;
	}

	/*
	 * Check the idle resources. The method is called back by the IdleChecker. 11/03/2014, Bing Li
	 */
	public void checkIdle() throws IOException
	{
		this.rscLock.lock();
		Resource rsc;
		// Get the queue keys of the idle resources. 11/03/2014, Bing Li
		Set<String> tKeys = this.idleMap.keySet();
		// Get the current time which is used to calculate the idle time length. 11/03/2014, Bing Li
		Date currentTime = Calendar.getInstance().getTime();
		// Check idle resources of each queue. 11/03/2014, Bing Li
		for (String key : tKeys)
		{
			// Check whether a specific queue is empty. If not, the loop continues to detect whether a resource is idle long enough. 11/03/2014, Bing Li
			while (this.idleMap.get(key).size() > 0)
			{
				// Check the first resource, but keep in the queue. 11/03/2014, Bing Li
				rsc = this.idleMap.get(key).peek();
				// Calculate the idle time length and compare it with the maximum idle time. 11/03/2014, Bing Li
				if (Time.getTimespanInMilliSecond(currentTime, rsc.getAccessedTime()) > this.maxIdleTime)
				{
					// Dequeue the resource that is idle long enough. 11/03/2014, Bing Li
					rsc = this.idleMap.get(key).poll();
					// Dispose the resource. 11/03/2014, Bing Li
					this.disposer.dispose(rsc);
				}
				else
				{
					// Since the first resource of the queue is the one that is idle longer than any others in the queue, it denotes that all of the resources in the queue are not idle longer than the upper limit. 11/03/2014, Bing Li
					break;
				}
			}
		}
		this.rscLock.unlock();
		// Notify the blocked thread that the size of the assigned resources is lowered such that it is time for it to get a resource it needs. A bunch of resources might be disposed during the procedure. It is reasonable to signal all rather than signal a single waiting thread. 11/03/2014, Bing Li 
		this.collaborator.signalAll();
	}
	
	/*
	 * Collect a resource. When the resource finishes its task, the method is invoked by the corresponding thread such that the resource can be reused. 11/03/2014, Bing Li
	 */
	public void collect(Resource rsc)
	{
		this.rscLock.lock();
		// Check whether the resource is contained in the busy map. 11/03/2014, Bing Li
		if (this.busyMap.containsKey(rsc.getHashKey()))
		{
			// If it is, it is required to remove it from the busy map. 11/03/2014, Bing Li
			this.busyMap.remove(rsc.getHashKey());
		}
		// Set the idle starting moment, which is used to calculate whether the resource is idle long enough. 11/03/2014, Bing Li
		rsc.setAccessedTime();
		// Check whether a queue is existed for the particular resource in the idle map. 11/03/2014, Bing Li
		if (!this.idleMap.containsKey(rsc.getObjectKey()))
		{
			// If the idle map does not consist of the queue for the type of the resource, it is required to create a new queue for it. 11/03/2014, Bing Li
			this.idleMap.put(rsc.getObjectKey(), new LinkedBlockingQueue<Resource>());
			this.idleMap.get(rsc.getObjectKey()).add(rsc);
		}
		else
		{
			// If the idle map consists of the queue for the type of the resource, just enqueue it into the queue. 11/03/2014, Bing Li
			this.idleMap.get(rsc.getObjectKey()).add(rsc);
		}
		this.rscLock.unlock();
		// Notify the thread that is blocked for the maximum size of the pool is reached. 11/03/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Get the resource by its type name. 09/17/2014, Bing Li
	 */
	public Resource get(String rscType) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Declare the resource to be returned to the thread that needs it. 11/03/2014, Bing Li
		Resource rsc = null;
		// Declare the idle size of all of the idle resources. 11/03/2014, Bing Li
		int idleSize;
		// Get the key of the resource type. 11/03/2014, Bing Li
		String queueKey = Tools.getHash(rscType);
		// The key of the longest idle queue. 11/03/2014, Bing Li 
		String longestQueueKey = UtilConfig.NO_QUEUE_KEY;
		// The size of the longest idle queue. 11/03/2014, Bing Li
		int longestQueueSize;

		// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 09/17/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			this.rscLock.lock();
			try
			{
				// Check whether the idle map consists of the queue for the specific resource. 11/03/2014, Bing Li
				if (this.idleMap.containsKey(queueKey))
				{
					// Check whether the queue for the resource is empty or not. 11/03/2014, Bing Li
					if (!this.idleMap.get(queueKey).isEmpty())
					{
						// If the queue is not empty, it denotes that idle resources of the specific type are available. Then, get the first one out from the queue. 11/03/2014, Bing Li
						rsc = this.idleMap.get(queueKey).poll();
						// Check whether it is null. 11/03/2014, Bing Li
						if (rsc != null)
						{
							// Set the busy starting time stamp. 11/03/2014, Bing Li
							rsc.setAccessedTime();
							// Put it into the busy map. 11/03/2014, Bing Li
							this.busyMap.put(rsc.getHashKey(), rsc);
							// Return the resource. 11/03/2014, Bing Li
							return rsc;
						}
					}
				}

				/*
				 * If the idle map does not contain the resource, it is necessary to create it if the pool size does not reach the upper limit. 11/03/2014, Bing Li
				 */
				
				// Initialize the idle resource size. 11/03/2014, Bing Li
				idleSize = 0;
				// Initialize the size of the longest idle queue. 11/03/2014, Bing Li
				longestQueueSize = 0;
				// Initialize the key of the longest idle queue. 11/03/2014, Bing Li
				longestQueueKey = UtilConfig.NO_QUEUE_KEY;
				// Calculate the count of all of the idle resources and the longest idle queue. 11/03/2014, Bing Li
				for (Map.Entry<String, Queue<Resource>> rscEntry : this.idleMap.entrySet())
				{
					// Calculate the count of all of the idle resources. 11/03/2014, Bing Li
					idleSize += rscEntry.getValue().size();
					// Calculate the longest idle queue size. 11/03/2014, Bing Li
					if (longestQueueSize < rscEntry.getValue().size())
					{
						// Get the size of the current longest queue. 11/03/2014, Bing Li
						longestQueueSize = rscEntry.getValue().size();
						// Get the key of the current longest queue. 11/03/2014, Bing Li
						longestQueueKey = rscEntry.getKey();
					}
				}

				// Check whether the sum of the count of busy and idle resources reach the upper limit of the pool. 11/03/2014, Bing Li 
				if (this.busyMap.size() + idleSize < this.poolSize)
				{
					// If the upper limit of the pool is not reached, create an instance of the resource. 11/03/2014, Bing Li
					rsc = this.creator.createClientInstance(rscType);
					// Put the newly created resource into the busy map. 11/03/2014, Bing Li
					this.busyMap.put(rsc.getHashKey(), rsc);
					// Return the newly created resource to the invoking thread. 11/03/2014, Bing Li
					return rsc;
				}
				else
				{
					// If the specified queue does not exist and the max size of pool is reached, it is proper to dispose an idle one in the longest queue. 11/03/2014, Bing Li
					if (!longestQueueKey.equals(UtilConfig.NO_QUEUE_KEY))
					{
						// Get an idle resource out from the longest queue. 11/03/2014, Bing Li
						rsc = this.idleMap.get(longestQueueKey).poll();
						// Dispose the first resource of the longest idle queue. 11/03/2014, Bing Li
						this.disposer.dispose(rsc);
						// Create a new resource. 11/03/2014, Bing Li
						rsc = this.creator.createClientInstance(rscType);
						// Put the newly created resource into the busy map. 11/03/2014, Bing Li
						this.busyMap.put(rsc.getHashKey(), rsc);
						// Return the newly created resource to the invoking thread. 11/03/2014, Bing Li
						return rsc;
					}
				}
			}
			finally
			{
				this.rscLock.unlock();
			}

			/*
			 * If no such resources in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include resource disposals and idle resources being available. 11/03/2014, Bing Li
			 */

			// Check whether the pool is shutdown. 11/03/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 11/03/2014, Bing Li
				this.collaborator.holdOn(UtilConfig.ONE_SECOND);
			}
		}
		return null;
	}
}
