package org.greatfree.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.Sync;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/*
 * This class is similar to RetrievablePool. In fact, it is a specific version of RetrievablePool. First, the resource managed by the pool is FreeClient. Second, it aims to initialize instances of FreeClient for not only output but also for input. 11/05/2014, Bing Li
 * 
 * In Java, to initialize an instance of FreeClient for reading remote data, an ObjectInputSream is required. However, to initialize the input stream, the remote end must have a corresponding initialized ObjectOutputStream instance. If the remote end's ObjectOutputStream is not initialized in time, the instances of FreeClient cannot be created by the pool since it gets stuck. 11/05/2014, Bing Li
 * 
 * To have the ability to read remotely, it must send a notification to the remote end to ensure the ObjectOutputStream has initialized. After that, an feedback message, InitReadFeedbackNotification, should be received if the output stream is available. Then, the instance of FreeClient can initialize ObjectInputStream. Therefore, it avoids the problem of getting stuck. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class FreeReaderPool
{
	// The map contains the instances of FreeClient that are being used. For each remote server, a children map of FreeClient is initialized. The parent key represents the unique ID of a remote server. The key of the children map is unique for each instance of FreeClient. The parent key is generated upon the IP address and the port of a remote server. Well, each client to the particular remote end has the children key, a hash value that is created randomly. 11/06/2014, Bing Li
	private Map<String, Map<String, FreeClient>> busyMap;
	// The map contains the instances of FreeClient that are idle temporarily. Similar to the busy map, for each type of resources, a children map is initialized. The parent key represents the remote server. The key of the children map is unique for each instance of FreeClient. 11/06/2014, Bing Li
	private Map<String, Map<String, FreeClient>> idleMap;
	// The map contains the IPPorts that are used to initialize particular instances of FreeClient. The key is identical to that of the instance of FreeClient. Therefore, it is fine to retrieve the IPPort to create the instance of FreeClient when no idle clients are available and the pool size does not reach the maximum value. 11/06/2014, Bing Li
	private Map<String, IPResource> sourceMap;

	// The lock is responsible for managing the operation on busyMap, idleMap and sourceMap atomic. 10/01/2014, Bing Li
	private ReentrantLock rscLock;
	// The Collaborator is used in the pool to work in the way of notify/wait. A thread has to wait when the pool is full. When resources are available, it can be notified by the collaborator. The collaborator also manages the termination of the pool. 11/06/2014, Bing Li
	private Sync collaborator;
	// The Timer controls the period to check the idle instance of FreeClient periodically. 11/06/2014, Bing Li
	private Timer checkTimer;
	// This is an instance of a class that is executed periodically to check idle clients. When a client is idle long enough, it is necessary to collect it. 11/06/2014, Bing Li
	private FreeReaderIdleChecker idleChecker;
	// The maximum time a client can be idle. 11/06/2014, Bing Li
	private long maxIdleTime;
	// The size of the clients the pool can contain. 11/06/2014, Bing Li
	private int poolSize;
	// When no idle clients are available and the pool size is not reached, it is allowed to create a new instance of FreeClient by the Creator. 11/06/2014, Bing Li
	private FreeClientCreator creator;
	// When an instance of FreeClient is idle long enough, it is collected or disposed by the Disposer. 11/06/2014, Bing Li
	private FreeClientDisposer disposer;

	// This is the collaborator that is not included in RetrievablePool. It serves to wait for the corresponding remote server's notification such that the instance of FreeClient can be returned. 11/06/2014, Bing Li
	private Sync initReadCollaborator;
	
	/*
	 * Initialize. 11/06/2014, Bing Li
	 */
	public FreeReaderPool(int poolSize)
	{
		this.busyMap = new ConcurrentHashMap<String, Map<String, FreeClient>>();
		this.idleMap = new ConcurrentHashMap<String, Map<String, FreeClient>>();
		this.sourceMap = new ConcurrentHashMap<String, IPResource>();

		this.rscLock = new ReentrantLock();
		this.collaborator = new Sync();
		this.poolSize = poolSize;
		this.creator = new FreeClientCreator();

		// It is possible that the pool does not need to check the idle clients. If so, it is unnecessary to initialize the timer. 11/06/2014, Bing Li
		this.checkTimer = UtilConfig.NO_TIMER;
		this.disposer = new FreeClientDisposer();
		
		this.initReadCollaborator = new Sync();
	}
	
	/*
	 * Shutdown the pool. 11/06/2014, Bing Li
	 */
	public void shutdown() throws IOException
	{
		/*
		// Set the shutdown flag to be true. Thus, the thread that runs the method of get() can be terminated. 11/06/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the thread that runs the method of get() to terminate and all of the threads that are waiting for the instances of FreeClient that they are unblocked. 11/06/2014, Bing Li 
		this.collaborator.signalAll();
		*/
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Dispose all of the busy clients. It might lose data if no relevant management approaches are adopted. 11/06/2014, Bing Li
		for (Map<String, FreeClient> resourceMap : this.busyMap.values())
		{
			for (FreeClient resource : resourceMap.values())
			{
				this.disposer.dispose(resource);
			}
		}
		this.busyMap.clear();

		// Dispose all of the idle FreeClients. It might lose data if no relevant management approaches are adopted. 11/06/2014, Bing Li
		for (Map<String, FreeClient> resourceMap : this.idleMap.values())
		{
			for (FreeClient resource : resourceMap.values())
			{
				this.disposer.dispose(resource);
			}
		}
		this.idleMap.clear();
		
		// Clear the sources. 11/06/2014, Bing Li
		this.sourceMap.clear();
		
		// Terminate the idle state checker that periodically runs. For it is possible that the checker is not initialized in some cases, it needs to check whether it is null or not. 11/06/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		// Terminate the timer that manages the period to run the idle state checker. For it is possible that the timer is not initialized in some cases, it needs to check whether it is null or not. 11/06/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
	}

	/*
	 * Initialize the idle state checker and the timer to manage idle instances of FreeClient periodically when needed. 11/06/2014, Bing Li
	 */
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		// Initialize the timer. 11/06/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the checker. 11/06/2014, Bing Li
		this.idleChecker = new FreeReaderIdleChecker(this);
		// Schedule the task of checking idle states of FreeClient. 11/06/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
		// Set the value of maximum idle time. 11/06/2014, Bing Li
		this.maxIdleTime = maxIdleTime;
	}
	
	/*
	 * Check the idle instances of FreeClient. The method is called back by the IdleChecker. 11/06/2014, Bing Li
	 */
	public void checkIdle() throws IOException
	{
		// Get the current time which is used to calculate the idle time length. 11/06/2014, Bing Li
		Date currentTime = Calendar.getInstance().getTime();

		// The map is used to keep the sorted instances of FreeClient to select the longest idle one conveniently. 11/06/2014, Bing Li
		Map<String, FreeClient> sortedResourceMap;

		// Check whether the idle instances of FreeClient are idle long enough time. 11/06/2014, Bing Li
		this.rscLock.lock();
		for (Map<String, FreeClient> resourceMap : this.idleMap.values())
		{
			// Sort the idle instances of FreeClient by their idle time moment in the ascending order and the results are saved in the map of sortedResourceMap. Thus, the resources that exceed the maximum time length most probably are listed ahead. 11/06/2014, Bing Li
			sortedResourceMap = CollectionSorter.sortByValue(resourceMap);
			// Check the sorted instances of FreeClient. 11/06/2014, Bing Li
			for (FreeClient resource : sortedResourceMap.values())
			{
				// Calculate the idle time length and compare it with the maximum idle time. 11/06/2014, Bing Li
				if (Time.getTimespanInMilliSecond(currentTime, resource.getAccessedTime()) > this.maxIdleTime)
				{
					// If the idle time exceeds the maximum idle time, it denotes that the instance of FreeClient is not needed. So it must be collected or disposed. 11/06/2014, Bing Li
					resourceMap.remove(resource.getHashKey());
					// Dispose the client by the disposer. 11/06/2014, Bing Li
					this.disposer.dispose(resource);
				}
				else
				{
					// If the idle time of the instance of FreeClient that has been idle for the longest time in the type does not exceed the maximum value, it denotes that all of the FreeClient instances of the type have not been idle long enough. Thus, it needs to detect other types of instances. 11/06/2014, Bing Li
					break;
				}
			}
		}

		// The loop detects whether one type of idle FreeClient instances is empty. If so, it needs to be removed from the idle map. 11/06/2014, Bing Li
		for (String objectKey : this.idleMap.keySet())
		{
			// Check whether one type of idle FreeClient instances is empty. 11/06/2014, Bing Li 
			if (this.idleMap.get(objectKey).size() <= 0)
			{
				// Remove the type of idle FreeClient instances that is empty from the map. 11/06/2014, Bing Li
				this.idleMap.remove(objectKey);
			}
		}
		this.rscLock.unlock();

		// Notify the blocked thread that the size of the assigned FreeClient instances is lowered such that it is time for it to get one it needs. A bunch of clients might be disposed during the procedure. It is reasonable to signal all rather than signal a single waiting thread. 11/06/2014, Bing Li 
		this.collaborator.signalAll();
	}
	
	/*
	 * Dispose an instance of FreeClient explicitly when it is not needed. Usually, it is not invoked by the pool but by the threads that consume the client. It happens when it is confirmed that the client is never needed in a specific case. 11/06/2014, Bing Li
	 */
	public void dispose(FreeClient client) throws IOException
	{
		// Check whether the client is null. 11/06/2014, Bing Li
		if (client != null)
		{
			// Check whether the pool is shutdown or not. If not, it must be managed by the rules of the pool. 11/06/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				this.rscLock.lock();
				// Check whether the type of FreeClient is contained in the busy map. 11/06/2014, Bing Li
				if (this.busyMap.containsKey(client.getObjectKey()))
				{
					// Check whether the client is contained in the busy map. 11/06/2014, Bing Li
					if (this.busyMap.get(client.getObjectKey()).containsKey(client.getHashKey()))
					{
						// Remove the client from the busy map. 11/06/2014, Bing Li
						this.busyMap.get(client.getObjectKey()).remove(client.getHashKey());
						// Check if the type of the client is empty in the busy map. 11/06/2014, Bing Li
						if (this.busyMap.get(client.getObjectKey()).size() <= 0)
						{
							// Remove the type of the FreeClient from the busy map.11/06/2014, Bing Li
							this.busyMap.remove(client.getObjectKey());
						}
					}
				}

				// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
				if (this.idleMap.containsKey(client.getObjectKey()))
				{
					// Check whether the idle map contains the specific instance of FreeClient. 11/06/2014, Bing Li
					if (this.idleMap.get(client.getObjectKey()).containsKey(client.getHashKey()))
					{
						// Remove the instance of FreeClient from the idle map. 11/06/2014, Bing Li
						this.idleMap.get(client.getObjectKey()).remove(client.getHashKey());
						// Check if the type of FreeClient is empty in the idle map. 11/06/2014, Bing Li
						if (this.idleMap.get(client.getObjectKey()).size() <= 0)
						{
							// Remove the type of FreeClient from the idle map. 11/06/2014, Bing Li
							this.idleMap.remove(client.getObjectKey());
						}
					}
				}
				// Dispose the resource eventually after it is managed following the rules of the pool. 11/06/2014, Bing Li
				this.disposer.dispose(client);
				this.rscLock.unlock();
				// Notify the thread that is blocked for the maximum size of the pool is reached. 11/06/2014, Bing Li
				this.collaborator.signal();
			}
			else
			{
				// If the pool is shutdown, the resource is disposed directly. 11/06/2014, Bing Li
				this.disposer.dispose(client);
			}
		}
	}
	
	/*
	 * Collect an instance of FreeClient. When the client finishes its task, the method is invoked by the corresponding thread such that the client can be reused. 11/06/2014, Bing Li
	 */
	public void collect(FreeClient client) throws NullPointerException
	{
		// Check whether the resource pool is shutdown. For the method is critical to the resource pool, it does not make sense to go ahead if the pool is shutdown. 11/06/2014, Bing Li
		if (client != null && !this.collaborator.isShutdown())
		{
			this.rscLock.lock();
			// Check whether the busy map contains the type of FreeClient. 11/06/2014, Bing Li
			if (this.busyMap.containsKey(client.getObjectKey()))
			{
				// Check whether the instance of FreeClient is contained in the busy map. 11/06/2014, Bing Li
				if (this.busyMap.get(client.getObjectKey()).containsKey(client.getHashKey()))
				{
					// Remove the instance of FreeClient to be collected. 11/06/2014, Bing Li
					this.busyMap.get(client.getObjectKey()).remove(client.getHashKey());
					// Check whether the type of FreeClient is empty in the busy map. If so, it is proper to remove it since it denotes that the type of FreeClient is not needed any longer. 11/06/2014, Bing Li
					if (this.busyMap.get(client.getObjectKey()).size() <= 0)
					{
						// Remove the type of FreeClient from the busy map. 11/06/2014, Bing Li
						this.busyMap.remove(client.getObjectKey());
					}
				}
			}
			
			// Set the accessed time stamp for the client. The time stamp is the idle starting moment of the client. It is used to calculate whether the client is idle for long enough. 11/06/2014, Bing Li
			client.setAccessedTime();
			// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
			if (!this.idleMap.containsKey(client.getObjectKey()))
			{
				// If the type of FreeClient is not contained in the idle map, it needs to add the type first. 11/06/2014, Bing Li
				this.idleMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
				// Add the instance of FreeClient to the idle map. 11/06/2014, Bing Li
				this.idleMap.get(client.getObjectKey()).put(client.getHashKey(), client);
			}
			else
			{
				// If the type of FreeClient is already existed in the idle map, just add the instance of FreeClient to the idle map. 11/06/2014, Bing Li
				this.idleMap.get(client.getObjectKey()).put(client.getHashKey(), client);
			}
			this.rscLock.unlock();
			// Notify the thread that is blocked for the maximum size of the pool is reached. 11/06/2014, Bing Li
			this.collaborator.signal();
		}
	}
	
	/*
	 * Remove the type of FreeClient explicitly by the key of the type. It is used in the case when a thread confirms that one type of FreeClient is not needed to be created in the pool. It is not used frequently. Just keep an interface for possible cases. 11/06/2014, Bing Li
	 */
	public void removeResource(String objectKey) throws IOException
	{
		this.rscLock.lock();
		// Check whether the type of FreeClient is existed in the busy map. 11/06/2014, Bing Li
		if (this.busyMap.containsKey(objectKey))
		{
			// Dispose all of the instances of FreeClient if they are in the busy map. 11/06/2014, Bing Li
			for (FreeClient rsc : this.busyMap.get(objectKey).values())
			{
				this.disposer.dispose(rsc);
			}
			// Remove the type of FreeClient from the busy map. 11/06/2014, Bing Li
			this.busyMap.remove(objectKey);
		}

		// Check whether the type of FreeClient is existed in the idle map. 11/06/2014, Bing Li
		if (this.idleMap.containsKey(objectKey))
		{
			// Dispose all of the instances of FreeClient if they are in the idle map. 11/06/2014, Bing Li
			for (FreeClient rsc : this.idleMap.get(objectKey).values())
			{
				this.disposer.dispose(rsc);
			}
			// Remove the type of FreeClient from the idle map. 11/06/2014, Bing Li
			this.idleMap.remove(objectKey);
		}
		this.rscLock.unlock();
		// Notify all of the threads that are waiting for FreeClient to keep on working if instances of FreeClient are available for the removal from the idle map. 11/06/2014, Bing Li
		this.collaborator.signalAll();
	}

	/*
	 * Check whether a specific instance of FreeClient is busy. 11/06/2014, Bing Li
	 */
	public boolean isBusy(FreeClient client)
	{
		this.rscLock.lock();
		try
		{
			// Check whether the type of FreeClient is existed in the busy map. 11/06/2014, Bing Li
			if (this.busyMap.containsKey(client.getObjectKey()))
			{
				// Check whether the specific instance of FreeClient is existed in the busy map if the type of FreeClient is existed in the map. 11/06/2014, Bing Li
				return this.busyMap.get(client.getObjectKey()).containsKey(client.getHashKey());
			}
			// If the type of FreeClient is not existed in the busy map, the client is not busy. 11/06/2014, Bing Li
			return false;
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Return all of the type keys of FreeClient. 11/06/2014, Bing Li
	 */
	public Set<String> getAllObjectKeys()
	{
		this.rscLock.lock();
		try
		{
			return this.sourceMap.keySet();
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Get the IPPort of a particular type of FreeClient. 11/06/2014, Bing Li
	 */
	public IPResource getSource(String objectKey)
	{
		this.rscLock.lock();
		try
		{
			// Check whether the source is available in the source map. 11/06/2014, Bing Li
			if (this.sourceMap.containsKey(objectKey))
			{
				// Return the source if it is existed in the source map. 11/06/2014, Bing Li
				return this.sourceMap.get(objectKey);
			}
			// Return null if the source is not available in the source map. 11/06/2014, Bing Li
			return null;
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Get the size of IPPorts. 11/06/2014, Bing Li
	 */
	public int getSourceSize()
	{
		this.rscLock.lock();
		try
		{
			return this.sourceMap.size();
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Get the size of the pool. 11/06/2014, Bing Li
	 */
	public synchronized int getPoolSize()
	{
		return this.poolSize;
	}
	
	/*
	 * Check whether a particular IPPort is available by the key of the IPPort. 11/06/2014, Bing Li
	 */
	public boolean isSourceExisted(String key)
	{
		this.rscLock.lock();
		try
		{
			return this.sourceMap.containsKey(key);
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Check whether a particular IPPort is available by the IPPort itself. 11/06/2014, Bing Li
	 */
	public boolean isSourceExisted(IPResource source)
	{
		this.rscLock.lock();
		try
		{
			return this.sourceMap.containsKey(source.getObjectKey());
		}
		finally
		{
			this.rscLock.unlock();
		}
	}
	
	/*
	 * Get the size of all of the types of busy instances of FreeClient at the moment when the method is invoked. 11/06/2014, Bing Li
	 */
	public int getBusyResourceSize()
	{
		int busyResourceCount = 0;
		this.rscLock.lock();
		try
		{
			for (Map<String, FreeClient> resMap : this.busyMap.values())
			{
				busyResourceCount += resMap.size();
			}
		}
		finally
		{
			this.rscLock.unlock();
		}
		return busyResourceCount;
	}
	
	/*
	 * Get the size of all of the types of idle instances of FreeClient at the moment when the method is invoked. 11/06/2014, Bing Li
	 */
	public int getIdleResourceSize()
	{
		int idleResourceCount = 0;
		this.rscLock.lock();
		try
		{
			for (Map<String, FreeClient> resMap : this.idleMap.values())
			{
				idleResourceCount += resMap.size();
			}
		}
		finally
		{
			this.rscLock.unlock();
		}
		return idleResourceCount;
	}

	/*
	 * After receiving feedback from the remote server, the method is called to notify the relevant to complete the initialization of ObjectInputStream of the FreeClient instance. 11/06/2014, Bing Li 
	 */
	public void notifyOutStreamDone()
	{
		this.initReadCollaborator.signal();
	}

	/*
	 * Get the instance of FreeClient by the IP/port of the remote server. The argument, nodeKey, is used to notify the remote server to retrieve the client to the local end such that the feedback can be sent. 11/06/2014, Bing Li
	 */
	public FreeClient get(String nodeKey, IPResource src) throws IOException
	{
		// The instance of FreeClient to be returned. 11/06/2014, Bing Li
		FreeClient client = null;
		// One particular type of FreeClient that is the same type as the requested one. 11/06/2014, Bing Li
		Map<String, FreeClient> resourceMap;
		// The hash key of the FreeClient instance that has the longest lifetime. 11/06/2014, Bing Li
		String oldestResourceKey;
		// The count of busy instances of FreeClient. 11/06/2014, Bing Li
		int busyResourceCount;
		// The count of idle instances of FreeClient. 11/06/2014, Bing Li
		int idleResourceCount;

		// Since the procedure to get an instance of FreeClient might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 11/06/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			// Initialize the value of busyResourceCount. 11/06/2014, Bing Li
			busyResourceCount = 0;
			// Initialize the value of idleResourceCount. 11/06/2014, Bing Li
			idleResourceCount = 0;

			this.rscLock.lock();
			try
			{
				/*
				 * Retrieve the FreeClient instance from the idle map first. 11/06/2014, Bing Li
				 */

				// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
				if (this.idleMap.containsKey(src.getObjectKey()))
				{
					// The type of idle FreeClient instances which are the candidates to be returned. 11/06/2014, Bing Li
					resourceMap = this.idleMap.get(src.getObjectKey());
					// Get the hash key of the client that is idle longer than any others from the idle FreeClient instances. 11/06/2014, Bing Li 
					oldestResourceKey = CollectionSorter.minValueKey(resourceMap);
					// Check whether the hash key is valid. 11/06/2014, Bing Li
					if (oldestResourceKey != null)
					{
						// Get the instance of FreeClient that is idle than any others by its hash key. 11/06/2014, Bing Li
						client = resourceMap.get(oldestResourceKey);
						// Set the accessed time of the client that is idle than any others to the current moment since the client will be used after it is returned. Now it becomes a busy one. 11/06/2014, Bing Li
						client.setAccessedTime();
						// Remove the instance of FreeClient from the idle map since it will become a busy one. 11/06/2014, Bing Li
						this.idleMap.get(src.getObjectKey()).remove(oldestResourceKey);
						// Check whether the type of FreeClient is empty in the idle map. 11/06/2014, Bing Li
						if (this.idleMap.get(src.getObjectKey()).size() <= 0)
						{
							// Remove the type of FreeClient from the idle map if no any specific instances of the type is in the idle map. 11/06/2014, Bing Li
							this.idleMap.remove(src.getObjectKey());
						}
						// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
						if (!this.busyMap.containsKey(client.getObjectKey()))
						{
							// If the type of FreeClient is not existed in the busy map, add the type and the particular instance. 11/06/2014, Bing Li
							this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
							this.busyMap.get(client.getObjectKey()).put(oldestResourceKey, client);
						}
						else
						{
							// If the type of FreeClient is existed in the busy map, add the particular instance. 11/06/2014, Bing Li
							this.busyMap.get(client.getObjectKey()).put(oldestResourceKey, client);
						}
					}
				}

				/*
				 * If the idle map does not contain the requested instance of FreeClient, it is necessary to initialize a new one. 11/06/2014, Bing Li
				 */

				// Check whether the instance of FreeClient is available after retrieving from the idle map. 11/06/2014, Bing Li
				if (client == null)
				{
					// Calculate the exact count of all of the busy instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
					for (Map<String, FreeClient> resMap : this.busyMap.values())
					{
						busyResourceCount += resMap.size();
					}

					// Calculate the exact count of all of the idle instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
					for (Map<String, FreeClient> resMap : this.idleMap.values())
					{
						idleResourceCount += resMap.size();
					}

					// Check whether the sum of the count of busy and idle instances of FreeClient reach the upper limit of the pool. 11/06/2014, Bing Li 
					if (busyResourceCount + idleResourceCount < this.poolSize)
					{
						// If the upper limit of the pool is not reached, it is time to create an instance of FreeClient by its IPPort. 11/06/2014, Bing Li
						client = this.creator.createClientInstance(src);
						// Check whether the newly created instance of FreeClient is valid. 11/06/2014, Bing Li
						if (client != null)
						{
							// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
							if (!this.busyMap.containsKey(client.getObjectKey()))
							{
								// If the type of FreeClient is not available in the busy map, add the type and the instance into it. 11/06/2014, Bing Li
								this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
								this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
							}
							else
							{
								// If the type of FreeClient is available in the busy map, add the instance into it. 11/06/2014, Bing Li
								this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
							}
							// Add the IPPort to the source map. 11/06/2014, Bing Li
							this.sourceMap.put(client.getObjectKey(), src);
						}

						// The line sends a message to the remote server. If it receives the message and send a feedback, it indicates that the ObjectOutputStream is initialized on the server. Then, the local ObjectInputStream can be initialized. It avoids the possible getting blocked. 11/06/2014, Bing Li
//						client.initRead(nodeKey);
						client.initRead();
						// Wait for feedback from the remote server. 11/06/2014, Bing Li
						this.initReadCollaborator.holdOn(UtilConfig.INIT_READ_WAIT_TIME);
						// After the feedback is received, the ObjectInputStream can be initialized. 11/06/2014, Bing Li
						client.setInputStream();
						// Return the instance of FreeClient to the thread. 11/06/2014, Bing Li
						return client;
					}
				}
				else
				{
					// If the instance of FreeClient is obtained from the idle map and then return it to the thread. 11/06/2014, Bing Li
					return client;
				}
			}
			finally
			{
				this.rscLock.unlock();
			}

			/*
			 * If no such instances of FreeClient are in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include FreeClient instances disposals and idle ones being available. 11/06/2014, Bing Li
			 */
			// Check whether the pool is shutdown. 11/06/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 11/06/2014, Bing Li
				this.collaborator.holdOn(UtilConfig.ONE_SECOND);
			}
		}
		return null;
	}

	/*
	 * Get the instance of FreeClient by the key of IPPort of the remote server. The argument, nodeKey, is used to notify the remote server to retrieve the client to the local end such that the feedback can be sent. 11/06/2014, Bing Li
	 */
	public FreeClient get(String nodeKey, String objectKey) throws IOException
	{
		// Check whether the key is valid. 11/06/2014, Bing Li
		if (objectKey != UtilConfig.NO_KEY)
		{
			// Declare an instance of IPPort. 11/06/2014, Bing Li
			IPResource src;
			// The instance of FreeClient to be returned. 11/06/2014, Bing Li
			FreeClient client = null;
			// One particular type of FreeClient that is the same type as the requested one. 11/06/2014, Bing Li
			Map<String, FreeClient> resourceMap;
			// The hash key of the FreeClient instance that has the longest lifetime. 11/06/2014, Bing Li
			String oldestResourceKey;
			// The count of busy instances of FreeClient. 11/06/2014, Bing Li
			int busyResourceCount;
			// The count of idle instances of FreeClient. 11/06/2014, Bing Li
			int idleResourceCount;

			// A flag to indicate whether the relevant IPPort is available. 11/06/2014, Bing Li
			boolean isSourceAvailable = false;
			// Since the procedure to get an instance of FreeClient might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 11/06/2014, Bing Li
			while (!this.collaborator.isShutdown())
			{
				this.rscLock.lock();
				try
				{
					// Check if the IPPort is available. 11/06/2014, Bing Li
					if (this.sourceMap.containsKey(objectKey))
					{
						// Set the flag that the IPPort is available. 11/06/2014, Bing Li
						isSourceAvailable = true;
						// Retrieve the IPPort. 11/06/2014, Bing Li
						src = this.sourceMap.get(objectKey);
						// Initialize the value of busyResourceCount. 11/06/2014, Bing Li
						busyResourceCount = 0;
						// Initialize the value of idleResourceCount. 11/06/2014, Bing Li
						idleResourceCount = 0;

						/*
						 * Retrieve the FreeClient instance from the idle map first. 11/06/2014, Bing Li
						 */
						
						// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
						if (this.idleMap.containsKey(src.getObjectKey()))
						{
							// The type of idle FreeClient instances which are the candidates to be returned. 11/06/2014, Bing Li
							resourceMap = this.idleMap.get(src.getObjectKey());
							// Get the hash key of the client that is idle longer than any others from the idle FreeClient instances. 11/06/2014, Bing Li 
							oldestResourceKey = CollectionSorter.minValueKey(resourceMap);
							// Check whether the hash key is valid. 11/06/2014, Bing Li
							if (oldestResourceKey != null)
							{
								// Get the instance of FreeClient that is idle than any others by its hash key. 11/06/2014, Bing Li
								client = resourceMap.get(oldestResourceKey);
								// Set the accessed time of the client that is idle than any others to the current moment since the client will be used after it is returned. Now it becomes a busy one. 11/06/2014, Bing Li
								client.setAccessedTime();
								// Remove the instance of FreeClient from the idle map since it will become a busy one. 11/06/2014, Bing Li
								this.idleMap.get(src.getObjectKey()).remove(oldestResourceKey);
								// Check whether the type of FreeClient is empty in the idle map. 11/06/2014, Bing Li
								if (this.idleMap.get(src.getObjectKey()).size() <= 0)
								{
									// Remove the type of FreeClient from the idle map if no any specific instances of the type is in the idle map. 11/06/2014, Bing Li
									this.idleMap.remove(src.getObjectKey());
								}
								// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
								if (!this.busyMap.containsKey(client.getObjectKey()))
								{
									// If the type of FreeClient is not existed in the busy map, add the type and the particular instance. 11/06/2014, Bing Li
									this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
									this.busyMap.get(client.getObjectKey()).put(oldestResourceKey, client);
								}
								else
								{
									// If the type of FreeClient is existed in the busy map, add the particular instance. 11/06/2014, Bing Li
									this.busyMap.get(client.getObjectKey()).put(oldestResourceKey, client);
								}
							}
						}

						/*
						 * If the idle map does not contain the requested instance of FreeClient, it is necessary to initialize a new one. 11/06/2014, Bing Li
						 */
						
						// Check whether the instance of FreeClient is available after retrieving from the idle map. 11/06/2014, Bing Li
						if (client == null)
						{
							// Calculate the exact count of all of the busy instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
							for (Map<String, FreeClient> resMap : this.busyMap.values())
							{
								busyResourceCount += resMap.size();
							}

							// Calculate the exact count of all of the idle instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
							for (Map<String, FreeClient> resMap : this.idleMap.values())
							{
								idleResourceCount += resMap.size();
							}

							// Check whether the sum of the count of busy and idle instances of FreeClient reach the upper limit of the pool. 11/06/2014, Bing Li 
							if (busyResourceCount + idleResourceCount < this.poolSize)
							{
								// If the upper limit of the pool is not reached, it is time to create an instance of FreeClient by its IPPort. 11/06/2014, Bing Li
								client = this.creator.createClientInstance(src);
								// Check whether the newly created instance of FreeClient is valid. 11/06/2014, Bing Li
								if (client != null)
								{
									// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
									if (!this.busyMap.containsKey(client.getObjectKey()))
									{
										// If the type of FreeClient is not available in the busy map, add the type and the instance into it. 11/06/2014, Bing Li
										this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
										this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
									}
									else
									{
										// If the type of FreeClient is available in the busy map, add the instance into it. 11/06/2014, Bing Li
										this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
									}
								}

								// The line sends a message to the remote server. If it receives the message, it indicates that the ObjectOutputStream is initialized on the server. Then, the local ObjectInputStream can be initialized. It avoids the possible getting blocked. 11/06/2014, Bing Li
//								client.initRead(nodeKey);
								client.initRead();
								// Wait for feedback from the remote server. 11/06/2014, Bing Li
								this.initReadCollaborator.holdOn(UtilConfig.INIT_READ_WAIT_TIME);
								// After the feedback is received, the ObjectInputStream can be initialized. 11/06/2014, Bing Li
								client.setInputStream();
							}
						}
						// Return the instance of FreeClient to the thread. 11/06/2014, Bing Li
						return client;
					}
				}
				finally
				{
					this.rscLock.unlock();
				}

				/*
				 * If no such instances of FreeClient are in the idle map and the upper limit of the pool is reached, the thread that invokes the method has to wait for future possible updates. The possible updates include FreeClient instances disposals and idle ones being available. 11/06/2014, Bing Li
				 */

				// Check if the IPPort is available. 11/06/2014, Bing Li
				if (isSourceAvailable)
				{
					// Check whether the pool is shutdown. 11/06/2014, Bing Li
					if (!this.collaborator.isShutdown())
					{
						// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 11/06/2014, Bing Li
						this.collaborator.holdOn(UtilConfig.ONE_SECOND);
					}
				}
				else
				{
					return ClientConfig.NO_CLIENT;
				}
			}
		}
		return ClientConfig.NO_CLIENT;
	}
}
