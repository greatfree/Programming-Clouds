package org.greatfree.reuse;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.concurrency.Sync;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/*
 * The class is a resource pool that aims to utilize the resources sufficiently with a lower cost. The pool is usually used for the resource of FreeClient. For each remote end, multiple FreeClients are initialized and managed by the pool. When it is necessary to interact with one remote end, it is convenient to obtain a FreeClient by the key or the initial values, i.e., the IP address and the port, that represent the remote end uniquely. That is why the pool is named the RetrievablePool. 09/02/2014, Bing Li
 * 
 * The Source is the initial values to create a resource. It must extend the object, FreeObject. 09/02/2014, Bing Li
 * 
 * The Resource is the resource that is managed by the pool. Also, it must derive from the object, FreeObject. 09/02/2014, Bing Li
 * 
 * The Creator aims to initialize an instance of a resource when no idle ones are available and the maximum pool size is not reached. It should implement the interface, ResourceCreatable. 09/02/2014, Bing Li
 * 
 * The Disposer is responsible for collecting or disposing the resources that are idle long enough. 09/02/2014, Bing Li
 * 
 */

// Created: 08/26/2014, Bing Li
public class RetrievablePool<Source extends FreeObject, Resource extends FreeObject, Creator extends Creatable<Source, Resource>, Disposer extends Disposable<Resource>>
{
//	private final static Logger log = Logger.getLogger("org.greatfree.reuse");
	
	// The map contains the resources that are being used. For each type of resources, a children map is initialized. The parent key represents the type of resources. The key of the children map is unique for each resource. For the case of FreeClient, the parent key is generated upon the IP address and the port of a remote end. Well, each client to the particular remote end has the children key, a hash value that is created randomly. 09/02/2014, Bing Li
	private Map<String, Map<String, Resource>> busyMap;
	// The map contains the resources that are idle temporarily. Similar to the busy map, for each type of resources, a children map is initialized. The parent key represents the type of resources. The key of the children map is unique for each resource. 09/02/2014, Bing Li
	private Map<String, Map<String, Resource>> idleMap;
	// The map contains the initial values that are used to initialize particular resources. The key is identical to that of the resource. Therefore, it is fine to retrieve the source to create the resource when no idle resources are available and the pool size does not reach the maximum value. For the case of FreeClient, the initial values are the IP address and the port. 09/02/2014, Bing Li
	private Map<String, Source> sourceMap;

	// The lock is responsible for managing the operations on busyMap, idleMap and sourceMap atomic. 10/12/2014, Bing Li
//	private ReentrantLock rscLock;
	// The Collaborator is used in the pool to work in the way of notify/wait. A thread has to wait when the pool is full. When resources are available, it can be notified by the collaborator. The collaborator also manages the termination of the pool. 09/02/2014, Bing Li
	private Sync collaborator;
	// The Timer controls the period to check the idle resources periodically. 11/06/2014, Bing Li
	private Timer checkTimer;
	// This is an instance of a class that is executed periodically to check idle resources. When a resource is idle long enough, it is necessary to collect it. 09/02/2014, Bing Li
	private RetrievableIdleChecker<Source, Resource, Creator, Disposer> idleChecker;
	// The maximum time a resource can be idle. 09/02/2014, Bing Li
	private long maxIdleTime;
	// The size of the resources the pool can contain. 09/02/2014, Bing Li
	private int poolSize;
	// When no idle resources are available and the pool size is not reached, it is allowed to create a new resource by the Creator. 09/02/2014, Bing Li
	private Creator creator;
	// When a resource is idle long enough, it is collected or disposed by the Disposer. 09/02/2014, Bing Li
	private Disposer disposer;

	/*
	 * Initialize. 09/02/2014, Bing Li
	 */
	public RetrievablePool(int poolSize, Creator creator, Disposer disposer)
	{
		this.busyMap = new ConcurrentHashMap<String, Map<String, Resource>>();
		this.idleMap = new ConcurrentHashMap<String, Map<String, Resource>>();
		this.sourceMap = new ConcurrentHashMap<String, Source>();

//		this.rscLock = new ReentrantLock();
		this.collaborator = new Sync();
		this.poolSize = poolSize;
		this.creator = creator;
		// It is possible that the pool does not need to check the idle resources. It happens when the pool is used in the case when the resource is not heavy and the consumed resources are low. If so, it is unnecessary to initialize the timer. 09/02/2014, Bing Li
		this.checkTimer = UtilConfig.NO_TIMER;
		this.disposer = disposer;
	}

	/*
	 * Shutdown the pool. 09/02/2014, Bing Li
	 */
	public void shutdown() throws IOException
	{
		/*
		// Set the shutdown flag to be true. Thus, the thread that runs the method of get() can be terminated. 09/02/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the thread that runs the method of get() to terminate and all of the threads that are waiting for the resources that they are unblocked. 09/02/2014, Bing Li 
		this.collaborator.signalAll();
		*/

		// Terminate the idle state checker that periodically runs. For it is possible that the checker is not initialized in some cases, it needs to check whether it is null or not. 09/02/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Terminate the timer that manages the period to run the idle state checker. For it is possible that the timer is not initialized in some cases, it needs to check whether it is null or not. 09/02/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Dispose all of the busy resources. It might lose data if no relevant management approaches are adopted. 09/02/2014, Bing Li
		for (Map<String, Resource> resourceMap : this.busyMap.values())
		{
			for (Resource resource : resourceMap.values())
			{
				this.disposer.dispose(resource);
			}
		}
		this.busyMap.clear();

		// Dispose all of the idle resources. It might lose data if no relevant management approaches are adopted. 09/02/2014, Bing Li
		for (Map<String, Resource> resourceMap : this.idleMap.values())
		{
			for (Resource resource : resourceMap.values())
			{
				this.disposer.dispose(resource);
			}
		}
		this.idleMap.clear();

		// Clear the sources. 09/02/2014, Bing Li
		this.sourceMap.clear();
	}

	/*
	 * Initialize the idle state checker and the timer to manage idle resources periodically when needed. 09/02/2014, Bing Li
	 */
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		// Initialize the timer. 09/02/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the checker. 09/02/2014, Bing Li
		this.idleChecker = new RetrievableIdleChecker<Source, Resource, Creator, Disposer>(this);
		// Schedule the task of checking idle states of resources. 09/02/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
		// Set the value of maximum idle time. 09/02/2014, Bing Li
		this.maxIdleTime = maxIdleTime;
	}

	/*
	 * Check the idle resources. The method is called back by the IdleChecker. 09/02/2014, Bing Li
	 */
	public void checkIdle() throws IOException
	{
		// Get the current time which is used to calculate the idle time length. 09/02/2014, Bing Li
		Date currentTime = Calendar.getInstance().getTime();
		
		// The map is used to keep the sorted resources to select the longest idle one conveniently. 09/02/2014, Bing Li
		Map<String, Resource> sortedResourceMap;

		// Check whether the idle resources are idle long enough time. 09/02/2014, Bing Li
//		this.rscLock.lock();
		for (Map<String, Resource> resourceMap : this.idleMap.values())
		{
			// Sort the idle resources by their idle time moment in the ascending order and the results are saved in the map of sortedResourceMap. Thus, the resources that exceed the maximum time length most probably are listed ahead. 09/02/2014, Bing Li
			sortedResourceMap = CollectionSorter.sortByValue(resourceMap);
			// Check the sorted resources. 09/02/2014, Bing Li
			for (Resource resource : sortedResourceMap.values())
			{
//				log.info("currentTime = " + currentTime);
//				log.info("accessed time = " + resource.getAccessedTime());
				// Calculate the idle time length and compare it with the maximum idle time. 09/02/2014, Bing Li
				if (Time.getTimespanInMilliSecond(currentTime, resource.getAccessedTime()) > this.maxIdleTime)
				{
					// If the idle time exceeds the maximum idle time, it denotes that the resource is not needed. So it must be collected or disposed. 09/06/2014, Bing Li
					resourceMap.remove(resource.getHashKey());
					// Dispose the resource by the disposer. 09/06/2014, Bing Li
					this.disposer.dispose(resource);
				}
				else
				{
					// If the idle time of the resource that has been idle for the longest time in the type does not exceed the maximum value, it denotes that all of the resources of the type have not been idle long enough. Thus, it needs to detect other types of resources. 09/06/2014, Bing Li
					break;
				}
			}
		}

		// The loop detects whether one type of idle resources is empty. If so, it needs to be removed from the idle map. 09/06/2014, Bing Li
		for (String objectKey : this.idleMap.keySet())
		{
			// Check whether one type of idle resources is empty. 09/06/2014, Bing Li 
			if (this.idleMap.get(objectKey).size() <= 0)
			{
				// Remove the type of idle resources that is empty from the map. 09/06/2014, Bing Li
				this.idleMap.remove(objectKey);
			}
		}
//		this.rscLock.unlock();

		// Notify the blocked thread that the size of the assigned resources is lowered such that it is time for it to get a resource it needs. A bunch of resources might be disposed during the procedure. It is reasonable to signal all rather than signal a single waiting thread. 09/06/2014, Bing Li 
		this.collaborator.signalAll();
	}

	/*
	 * Dispose a resource explicitly when it is not needed. Usually, it is not invoked by the pool but by the threads that consume the resource. It happens when it is confirmed that the resource is never needed in a specific case. 09/06/2014, Bing Li
	 */
	public void dispose(Resource res) throws IOException
	{
		// Check whether the resource is null. 09/06/2014, Bing Li
		if (res != null)
		{
			// Check whether the pool is shutdown or not. If not, it must be managed by the rules of the pool. 09/06/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
//				this.rscLock.lock();
				// Check whether the type of the resource is contained in the busy map. 09/06/2014, Bing Li
				if (this.busyMap.containsKey(res.getObjectKey()))
				{
					// Check whether the resource is contained in the busy map. 09/06/2014, Bing Li
					if (this.busyMap.get(res.getObjectKey()).containsKey(res.getHashKey()))
					{
						// Remove the resource from the busy map. 09/06/2014, Bing Li
						this.busyMap.get(res.getObjectKey()).remove(res.getHashKey());
						// Check if the type of the resource is empty in the busy map. 09/06/2014, Bing Li
						if (this.busyMap.get(res.getObjectKey()).size() <= 0)
						{
							// Remove the type of the resource from the busy map. 09/06/2014, Bing Li
							this.busyMap.remove(res.getObjectKey());
						}
					}
				}

				// Check whether the idle map contains the type of the resource. 09/06/2014, Bing Li
				if (this.idleMap.containsKey(res.getObjectKey()))
				{
					// Check whether the idle map contains the specific resource. 09/06/2014, Bing Li
					if (this.idleMap.get(res.getObjectKey()).containsKey(res.getHashKey()))
					{
						// Remove the resource from the idle map. 09/06/2014, Bing Li
						this.idleMap.get(res.getObjectKey()).remove(res.getHashKey());
						// Check if the type of the resource is empty in the idle map. 09/06/2014, Bing Li
						if (this.idleMap.get(res.getObjectKey()).size() <= 0)
						{
							// Remove the type of the resource from the idle map. 09/06/2014, Bing Li
							this.idleMap.remove(res.getObjectKey());
						}
					}
				}

				// Dispose the resource eventually after it is managed following the rules of the pool. 09/06/2014, Bing Li
				this.disposer.dispose(res);
				
//				this.rscLock.unlock();

				// Notify the thread that is blocked for the maximum size of the pool is reached. 09/06/2014, Bing Li
				this.collaborator.signal();
			}
			else
			{
				// If the pool is shutdown, the resource is disposed directly. 09/06/2014, Bing Li
				this.disposer.dispose(res);
			}
		}
	}

	/*
	 * Collect a resource. When the resource finishes its task, the method is invoked by the corresponding thread such that the resource can be reused. 09/06/2014, Bing Li
	 */
	public void collect(Resource res)
	{
		// Check whether the resource pool is shutdown. For the method is critical to the resource pool, it does not make sense to go ahead if the pool is shutdown. 09/06/2014, Bing Li
		if (res != null && !this.collaborator.isShutdown())
		{
//			this.rscLock.lock();
			// Check whether the busy map contains the type of the resource. 09/06/2014, Bing Li
			if (this.busyMap.containsKey(res.getObjectKey()))
			{
				// Check whether the resource is contained in the busy map. 09/06/2014, Bing Li
				if (this.busyMap.get(res.getObjectKey()).containsKey(res.getHashKey()))
				{
					// Remove the resource to be collected. 09/06/2014, Bing Li
					this.busyMap.get(res.getObjectKey()).remove(res.getHashKey());
					// Check whether the type of the resource is empty in the busy map. If so, it is proper to remove it since it denotes that the type of the resource is not needed any longer. 09/06/2014, Bing Li
					if (this.busyMap.get(res.getObjectKey()).size() <= 0)
					{
						// Remove the type of the resource from the busy map. 09/06/2014, Bing Li
						this.busyMap.remove(res.getObjectKey());
					}
				}
			}

			// Set the accessed time stamp for the resource. The time stamp is the idle starting moment of the resource. It is used to calculate whether the resource is idle for long enough. 09/06/2014, Bing Li
			res.setAccessedTime();
			// Check whether the idle map contains the type of the resource. 09/06/2014, Bing Li
			if (!this.idleMap.containsKey(res.getObjectKey()))
			{
				// If the type of the resource is not contained in the idle map, it needs to add the type first. 09/06/2014, Bing Li
				this.idleMap.put(res.getObjectKey(), new ConcurrentHashMap<String, Resource>());
				// Add the resource to the idle map. 09/06/2014, Bing Li
				this.idleMap.get(res.getObjectKey()).put(res.getHashKey(), res);
			}
			else
			{
				// If the type of the resource is already existed in the idle map, just add the resource to the idle map. 09/06/2014, Bing Li
				this.idleMap.get(res.getObjectKey()).put(res.getHashKey(), res);
			}
//			this.rscLock.unlock();
			
			// Notify the thread that is blocked for the maximum size of the pool is reached. 09/06/2014, Bing Li
			this.collaborator.signal();
		}
	}

	/*
	 * Remove the type of resources explicitly by the key of the type. It is used in the case when a thread confirms that one type of resources is not needed to be created in the pool. It is not used frequently. Just keep an interface for possible cases. 09/06/2014, Bing Li
	 */
	public void removeResource(String objectKey) throws IOException
	{
//		this.rscLock.lock();

		// Check whether the type of resources is existed in the busy map. 09/17/2014, Bing Li
		if (this.busyMap.containsKey(objectKey))
		{
			// Dispose all of the resources of the type if they are in the busy map. 09/17/2014, Bing Li
			for (Resource rsc : this.busyMap.get(objectKey).values())
			{
				this.disposer.dispose(rsc);
			}
			// Remove the type of resources from the busy map. 09/17/2014, Bing Li
			this.busyMap.remove(objectKey);
		}
		
		// Check whether the type of resources is existed in the idle map. 09/17/2014, Bing Li
		if (this.idleMap.containsKey(objectKey))
		{
			// Dispose all of the resources of the type if they are in the idle map. 09/17/2014, Bing Li
			for (Resource rsc : this.idleMap.get(objectKey).values())
			{
				this.disposer.dispose(rsc);
			}
			// Remove the type of resources from the idle map. 09/17/2014, Bing Li
			this.idleMap.remove(objectKey);
		}
//		this.rscLock.unlock();

		// Notify all of the threads that are waiting for resources to keep on working if resources are available for the removal from the idle map. 09/17/2014, Bing Li
		this.collaborator.signalAll();
	}
	
	public int getClientSize()
	{
		return this.busyMap.size() + this.idleMap.size();
	}

	/*
	 * Check whether a specific resource is busy. 09/17/2014, Bing Li
	 */
	public boolean isBusy(Resource resource)
	{
		/*
		this.rscLock.lock();
		try
		{
			// Check whether the type of the resource is existed in the busy map. 09/17/2014, Bing Li
			if (this.busyMap.containsKey(resource.getObjectKey()))
			{
				// Check whether the specific resource is existed in the busy map if the type of the resource is existed in the map. 09/17/2014, Bing Li
				return this.busyMap.get(resource.getObjectKey()).containsKey(resource.getHashKey());
			}
			// If the type of the resource is not existed in the busy map, the resource is not busy. 09/17/2014, Bing Li
			return false;
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		// Check whether the type of the resource is existed in the busy map. 09/17/2014, Bing Li
		if (this.busyMap.containsKey(resource.getObjectKey()))
		{
			// Check whether the specific resource is existed in the busy map if the type of the resource is existed in the map. 09/17/2014, Bing Li
			return this.busyMap.get(resource.getObjectKey()).containsKey(resource.getHashKey());
		}
		// If the type of the resource is not existed in the busy map, the resource is not busy. 09/17/2014, Bing Li
		return false;
	}

	/*
	 * Return all of the type keys of resources. 09/17/2014, Bing Li
	 */
	public Set<String> getAllObjectKeys()
	{
		/*
		this.rscLock.lock();
		try
		{
			return this.sourceMap.keySet();
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		return this.sourceMap.keySet();
	}

	/*
	 * Get the source of a particular type of resources. 09/17/2014, Bing Li
	 */
	public Source getSource(String objectKey)
	{
		/*
		this.rscLock.lock();
		try
		{
			// Check whether the source is available in the source map. 09/17/2014, Bing Li
			if (this.sourceMap.containsKey(objectKey))
			{
				// Return the source if it is existed in the source map. 09/17/2014, Bing Li
				return this.sourceMap.get(objectKey);
			}
			// Return null if the source is not available in the source map. 09/17/2014, Bing Li
			return null;
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		// Check whether the source is available in the source map. 09/17/2014, Bing Li
		if (this.sourceMap.containsKey(objectKey))
		{
			// Return the source if it is existed in the source map. 09/17/2014, Bing Li
			return this.sourceMap.get(objectKey);
		}
		// Return null if the source is not available in the source map. 09/17/2014, Bing Li
		return null;
	}
	
	public void removeSource(String objectKey)
	{
		/*
		this.rscLock.lock();
		try
		{
			if (this.sourceMap.containsKey(objectKey))
			{
				this.sourceMap.remove(objectKey);
			}
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		if (this.sourceMap.containsKey(objectKey))
		{
			this.sourceMap.remove(objectKey);
		}
	}

	/*
	 * Get the size of sources. 09/17/2014, Bing Li
	 */
	public int getSourceSize()
	{
		/*
		this.rscLock.lock();
		try
		{
			return this.sourceMap.size();
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		return this.sourceMap.size();
	}

	/*
	 * Get the size of the pool. 11/03/2014, Bing Li
	 */
	public synchronized int getPoolSize()
	{
		return this.poolSize;
	}
	
	/*
	 * Check whether a particular source is available by the key of the source. 09/17/2014, Bing Li
	 */
	public boolean isSourceExisted(String key)
	{
		/*
		this.rscLock.lock();
		try
		{
			return this.sourceMap.containsKey(key);
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		return this.sourceMap.containsKey(key);
	}

	/*
	 * Check whether a particular source is available by the source itself. 09/17/2014, Bing Li
	 */
	public boolean isSourceExisted(Source src)
	{
		/*
		this.rscLock.lock();
		try
		{
			return this.sourceMap.containsKey(src.getObjectKey());
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		return this.sourceMap.containsKey(src.getObjectKey());
	}

	/*
	 * Check whether one particular resource is available or not. 05/08/2017, Bing Li
	 */
	public boolean isResourceExisted(String key)
	{
		/*
		this.rscLock.lock();
		try
		{
			return this.busyMap.containsKey(key) || this.idleMap.containsKey(key);
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		return this.busyMap.containsKey(key) || this.idleMap.containsKey(key);
	}

	/*
	 * Get the size of all of the types of busy resources at the moment when the method is invoked. 09/17/2014, Bing Li
	 */
	public int getBusyResourceSize()
	{
		int busyResourceCount = 0;
		/*
		this.rscLock.lock();
		try
		{
			for (Map<String, Resource> resMap : this.busyMap.values())
			{
				busyResourceCount += resMap.size();
			}
			return busyResourceCount;
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		for (Map<String, Resource> resMap : this.busyMap.values())
		{
			busyResourceCount += resMap.size();
		}
		return busyResourceCount;
	}
	
	/*
	 * Get the size of all of the types of idle resources at the moment when the method is invoked. 11/03/2014, Bing Li
	 */
	public int getIdleResourceSize()
	{
		int idleResourceCount = 0;
		/*
		this.rscLock.lock();
		try
		{
			for (Map<String, Resource> resMap : this.idleMap.values())
			{
				idleResourceCount += resMap.size();
			}
			return idleResourceCount;
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		for (Map<String, Resource> resMap : this.idleMap.values())
		{
			idleResourceCount += resMap.size();
		}
		return idleResourceCount;
	}

	/*
	 * Add source. 05/08/2017, Bing Li
	 */
	public void addSource(Source source)
	{
		/*
		this.rscLock.lock();
		try
		{
			this.sourceMap.put(source.getObjectKey(), source);
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		this.sourceMap.put(source.getObjectKey(), source);
//		log.info("source map keys' size = " + this.sourceMap.keySet().size());
	}
	
	public void clearSource()
	{
//		log.info("clearSource is executed ...");
		/*
		this.rscLock.lock();
		try
		{
			this.sourceMap.clear();
			this.busyMap.clear();
			this.idleMap.clear();
		}
		finally
		{
			this.rscLock.unlock();
		}
		*/
		this.sourceMap.clear();
		this.busyMap.clear();
		this.idleMap.clear();
	}

	/*
	 * Get the resource by its source. 09/17/2014, Bing Li
	 */
	public synchronized Resource get(Source source) throws IOException
	{
		// The resource to be returned. 09/17/2014, Bing Li
		Resource rsc = null;
		// One particular type of resources that is the same type as the requested one. 09/17/2014, Bing Li
		Map<String, Resource> resourceMap;
		// The hash key of the resource that has the longest lifetime. 09/17/2014, Bing Li
		String oldestResourceKey;
		// The count of busy resources. 09/17/2014, Bing Li
		int busyResourceCount;
		// The count of idle resources 09/17/2014, Bing Li
		int idleResourceCount;

		// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 09/17/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
			busyResourceCount = 0;
			// Initialize the value of idleResourceCount. 09/17/2014, Bing Li
			idleResourceCount = 0;
			
//			this.rscLock.lock();
//			try
//			{
			/*
			 * Retrieve the resource from the idle map first. 09/17/2014, Bing Li
			 */

			// Check whether the idle map contains the type of the resource. 09/17/2014, Bing Li
			if (this.idleMap.containsKey(source.getObjectKey()))
			{
				// The type of idle resources which are the candidates to be returned. 09/17/2014, Bing Li
				resourceMap = this.idleMap.get(source.getObjectKey());
				// Get the hash key of the resource that is idle longer than any others from the idle resources. 09/17/2014, Bing Li 
				oldestResourceKey = CollectionSorter.minValueKey(resourceMap);
				// Check whether the hash key is valid. 09/17/2014, Bing Li
				if (oldestResourceKey != null)
				{
					// Get the resource that is idle than any others by its hash key. 09/17/2014, Bing Li
					rsc = resourceMap.get(oldestResourceKey);
					// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
					rsc.setAccessedTime();
					// Remove the resource from the idle map since it will become a busy one. 09/17/2014, Bing Li
					this.idleMap.get(source.getObjectKey()).remove(oldestResourceKey);
					// Check whether the type of resources is empty in the idle map. 09/17/2014, Bing Li
					if (this.idleMap.get(source.getObjectKey()).size() <= 0)
					{
						// Remove the type of resources from the idle map if no any specific resources of the type is in the idle map. 09/17/2014, Bing Li
						this.idleMap.remove(source.getObjectKey());
					}
					// Check whether the type of resources is available in the busy map. 09/17/2014, Bing Li
					if (!this.busyMap.containsKey(rsc.getObjectKey()))
					{
						// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
						this.busyMap.put(rsc.getObjectKey(), new ConcurrentHashMap<String, Resource>());
						this.busyMap.get(rsc.getObjectKey()).put(oldestResourceKey, rsc);
					}
					else
					{
						// If the type of resources is existed in the busy map, add the particular resource. 09/17/2014, Bing Li
						this.busyMap.get(rsc.getObjectKey()).put(oldestResourceKey, rsc);
					}
				}
			}

			/*
			 * If the idle map does not contain the requested resource, it is necessary to initialize a new one. 09/17/2014, Bing Li
			 */

			// Check whether the resource is available after retrieving from the idle map. 10/12/2014, Bing Li
			if (rsc == null)
			{
				// Calculate the exact count of all of the busy resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
				for (Map<String, Resource> resMap : this.busyMap.values())
				{
					busyResourceCount += resMap.size();
				}
				
				// Calculate the exact count of all of the idle resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
				for (Map<String, Resource> resMap : this.idleMap.values())
				{
					idleResourceCount += resMap.size();
				}
				
				// Check whether the sum of the count of busy and idle resources reach the upper limit of the pool. 09/17/2014, Bing Li 
				if (busyResourceCount + idleResourceCount < this.poolSize)
				{
					// If the upper limit of the pool is not reached, it is time to create an instance by its source. 09/17/2014, Bing Li
					rsc = this.creator.createResourceInstance(source);
					// Check whether the newly created instance is valid. 09/17/2014, Bing Li
					if (rsc != null)
					{
						// Check whether the type of the resource is available in the busy map. 09/17/2014, Bing Li
						if (!this.busyMap.containsKey(rsc.getObjectKey()))
						{
							// If the type of the resource is not available in the busy map, add the type and the resource into it. 09/17/2014, Bing Li
							this.busyMap.put(rsc.getObjectKey(), new ConcurrentHashMap<String, Resource>());
							this.busyMap.get(rsc.getObjectKey()).put(rsc.getHashKey(), rsc);
						}
						else
						{
							// If the type of the resource is available in the busy map, add the resource into it. 09/17/2014, Bing Li
							this.busyMap.get(rsc.getObjectKey()).put(rsc.getHashKey(), rsc);
						}
						// Add the source to the source map. 09/17/2014, Bing Li
						this.sourceMap.put(rsc.getObjectKey(), source);
					}
					// Return the resource to the thread. 09/17/2014, Bing Li
					return rsc;
				}
			}
			else
			{
				// If the resource is obtained from the idle map and then return the resource to the thread. 09/17/2014, Bing Li
				return rsc;
			}
//			}
//			finally
//			{
//				this.rscLock.unlock();
//			}
			
			/*
			 * If no such resources are in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include resource disposals and idle resources being available. 09/17/2014, Bing Li
			 */
			// Check whether the pool is shutdown. 09/17/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 09/17/2014, Bing Li
				this.collaborator.holdOn(UtilConfig.ONE_SECOND);
			}
		}
		return null;
	}

	/*
	 * Get the resource by its type key. 09/17/2014, Bing Li
	 */
	public Resource get(String objectKey) throws IOException
	{
		// Check whether the key is valid. 09/17/2014, Bing Li
		if (objectKey != UtilConfig.NO_KEY)
		{
			Source src;
			// The resource to be returned. 09/17/2014, Bing Li
			Resource rsc = null;
			// One particular type of resources that is the same type as the requested one. 09/17/2014, Bing Li
			Map<String, Resource> resources;
			// The hash key of the resource that has the longest lifetime. 09/17/2014, Bing Li
			String oldestResourceKey;
			// The count of busy resources. 09/17/2014, Bing Li
			int busyResourceCount;
			// The count of idle resources 09/17/2014, Bing Li
			int idleResourceCount;
			boolean isSourceAvailable = false;

			// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 09/17/2014, Bing Li
			while (!this.collaborator.isShutdown())
			{
//				this.rscLock.lock();
//				try
//				{
				// Check whether the type of the resource is existed in the source map. 09/17/2014, Bing Li
				if (this.sourceMap.containsKey(objectKey))
				{
					// Set the flag that the source is available. 11/03/2014, Bing Li
					isSourceAvailable = true;
					// Retrieve the source. 11/03/2014, Bing Li
					src = this.sourceMap.get(objectKey);
					// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
					busyResourceCount = 0;
					// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
					idleResourceCount = 0;

					/*
					 * Retrieve the resource from the idle map first. 09/17/2014, Bing Li
					 */

					// Check whether the idle map contains the type of the resource. 09/17/2014, Bing Li
					if (this.idleMap.containsKey(src.getObjectKey()))
					{
						// The type of idle resources which are the candidates to be returned. 09/17/2014, Bing Li
						resources = this.idleMap.get(src.getObjectKey());
						// Get the hash key of the resource that is idle longer than any others from the idle resources. 09/17/2014, Bing Li 
						oldestResourceKey = CollectionSorter.minValueKey(resources);
						// Check whether the hash key is valid. 09/17/2014, Bing Li
						if (oldestResourceKey != null)
						{
							// Get the resource that is idle than any others by its hash key. 09/17/2014, Bing Li
							rsc = resources.get(oldestResourceKey);

							// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
							rsc.setAccessedTime();
							
							// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
							this.idleMap.get(src.getObjectKey()).remove(oldestResourceKey);
							// Check whether the type of resources is empty in the idle map. 09/17/2014, Bing Li
							if (this.idleMap.get(src.getObjectKey()).size() <= 0)
							{
								// Remove the type of resources from the idle map if no any specific resources of the type is in the idle map. 09/17/2014, Bing Li
								this.idleMap.remove(src.getObjectKey());
							}

							// Check whether the type of resources is available in the busy map. 09/17/2014, Bing Li
							if (!this.busyMap.containsKey(rsc.getObjectKey()))
							{
								// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
								this.busyMap.put(rsc.getObjectKey(), new ConcurrentHashMap<String, Resource>());
								this.busyMap.get(rsc.getObjectKey()).put(oldestResourceKey, rsc);
							}
							else
							{
								// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
								this.busyMap.get(rsc.getObjectKey()).put(oldestResourceKey, rsc);
							}
						}
					}

					/*
					 * If the idle map does not contain the requested resource, it is necessary to initialize a new one. 09/17/2014, Bing Li
					 */

					// Check whether the resource is available when retrieving from the idle map. 10/12/2014, Bing Li
					if (rsc == null)
					{
						// Calculate the exact count of all of the busy resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
						for (Map<String, Resource> resMap : this.busyMap.values())
						{
							busyResourceCount += resMap.size();
						}

						// Calculate the exact count of all of the idle resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
						for (Map<String, Resource> resMap : this.idleMap.values())
						{
							idleResourceCount += resMap.size();
						}

						// Check whether the sum of the count of busy and idle resources reach the upper limit of the pool. 09/17/2014, Bing Li 
						if (busyResourceCount + idleResourceCount < this.poolSize)
						{
							// If the upper limit of the pool is not reached, it is time to create an instance by its source. 09/17/2014, Bing Li
							rsc = this.creator.createResourceInstance(src);
							// Check whether the newly created instance is valid. 09/17/2014, Bing Li
							if (rsc != null)
							{
								// Check whether the type of the resource is available in the busy map. 09/17/2014, Bing Li
								if (!this.busyMap.containsKey(rsc.getObjectKey()))
								{
									// If the type of the resource is not available in the busy map, add the type and the resource into it. 09/17/2014, Bing Li
									this.busyMap.put(rsc.getObjectKey(), new ConcurrentHashMap<String, Resource>());
									this.busyMap.get(rsc.getObjectKey()).put(rsc.getHashKey(), rsc);
								}
								else
								{
									// If the type of the resource is available in the busy map, add the resource into it. 09/17/2014, Bing Li
									this.busyMap.get(rsc.getObjectKey()).put(rsc.getHashKey(), rsc);
								}
							}
						}
					}
					// Return the resource to the invoker. 09/17/2014, Bing Li
					return rsc;
				}
//				}
//				finally
//				{
//					this.rscLock.unlock();
//				}

				// If the source is available, it is expeced to wait for idle resource. 11/03/2014, Bing Li
				if (isSourceAvailable)
				{
					/*
					 * If no such resources in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include resource disposals and idle resources being available. 09/17/2014, Bing Li
					 */

					// Check whether the pool is shutdown. 09/17/2014, Bing Li
					if (!this.collaborator.isShutdown())
					{
						// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 09/17/2014, Bing Li
						this.collaborator.holdOn(UtilConfig.ONE_SECOND);
					}
				}
			}
		}
		// Return null if the key is invalid. 09/17/2014, Bing Li
		return null;
	}
}