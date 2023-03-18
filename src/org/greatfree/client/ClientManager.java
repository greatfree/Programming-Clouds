package org.greatfree.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.greatfree.concurrency.Sync;
import org.greatfree.message.LeaveRequest;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/**
 * 
 * The FreeClientPool is employed by SyncRemoteEventer.
 * 
 * And, the RemoteReader uses the FreeReaderPool.
 * 
 * Both of them are independent of each other such that they need to consume the TCP connections at the same time.
 * 
 * It is not reasonable in terms of resource consumption.
 * 
 * A better solution is that both of them share one pool.
 * 
 * For that, the FreeClientPool and the FreeReaderPool are merged in the version.
 * 
 * 
 * @author libing
 * 
 * 10/09/2022
 *
 */
final class ClientManager
{
	private final static Logger log = Logger.getLogger("org.greatfree.client");

	// The map contains the instances of FreeClient that are being used. For each remote server, a children map of FreeClient is initialized. The parent key represents the unique ID of a remote server. The key of the children map is unique for each instance of FreeClient. The parent key is generated upon the IP address and the port of a remote server. Well, each client to the particular remote end has the children key, a hash value that is created randomly. 11/06/2014, Bing Li
	private Map<String, Map<String, FreeClient>> busyMap;
	// The map contains the instances of FreeClient that are idle temporarily. Similar to the busy map, for each type of resources, a children map is initialized. The parent key represents the remote server. The key of the children map is unique for each instance of FreeClient. 11/06/2014, Bing Li
	private Map<String, Map<String, FreeClient>> idleMap;
	// The map contains the IPPorts that are used to initialize particular instances of FreeClient. The key is identical to that of the instance of FreeClient. Therefore, it is fine to retrieve the IPPort to create the instance of FreeClient when no idle clients are available and the pool size does not reach the maximum value. 11/06/2014, Bing Li
	private Map<String, IPResource> resourceMap;

	// The Collaborator is used in the pool to work in the way of notify/wait. A thread has to wait when the pool is full. When resources are available, it can be notified by the collaborator. The collaborator also manages the termination of the pool. 11/06/2014, Bing Li
	private Sync collaborator;
	// The Timer controls the period to check the idle instance of FreeClient periodically. 11/06/2014, Bing Li
	private Timer checkTimer;
	// This is an instance of a class that is executed periodically to check idle clients. When a client is idle long enough, it is necessary to collect it. 11/06/2014, Bing Li
	private ClientManagerIdleChecker idleChecker;
	// The maximum time a client can be idle. 11/06/2014, Bing Li
	private long maxIdleTime;
	// The size of the clients the pool can contain. 11/06/2014, Bing Li
	private int poolSize;
	// When no idle clients are available and the pool size is not reached, it is allowed to create a new instance of FreeClient by the Creator. 11/06/2014, Bing Li
	private FreeClientCreator creator;
	// When an instance of FreeClient is idle long enough, it is collected or disposed by the Disposer. 11/06/2014, Bing Li
	private FreeClientDisposer disposer;

	// This is the collaborator that is not included in RetrievablePool. It serves to wait for the corresponding remote server's notification such that the instance of FreeClient can be returned. 11/06/2014, Bing Li
//	private Sync initReadCollaborator;
	
	/*
	 * Initialize. 11/06/2014, Bing Li
	 */
	public ClientManager(int poolSize)
	{
		this.busyMap = new ConcurrentHashMap<String, Map<String, FreeClient>>();
		this.idleMap = new ConcurrentHashMap<String, Map<String, FreeClient>>();
		this.resourceMap = new ConcurrentHashMap<String, IPResource>();

		this.collaborator = new Sync();
		this.poolSize = poolSize;
		this.creator = new FreeClientCreator();

		// It is possible that the pool does not need to check the idle clients. If so, it is unnecessary to initialize the timer. 11/06/2014, Bing Li
		this.checkTimer = UtilConfig.NO_TIMER;
		this.disposer = new FreeClientDisposer();
		
//		this.initReadCollaborator = new Sync();
	}
	
	/*
	 * Shutdown the pool. 11/06/2014, Bing Li
	 */
	public void shutdown()
	{
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();
		
		// Dispose all of the busy clients. It might lose data if no relevant management approaches are adopted. 11/06/2014, Bing Li
		for (Map<String, FreeClient> clients : this.busyMap.values())
		{
			for (FreeClient client : clients.values())
			{
				/*
				 * The line delays some time to ensure previous data is sent out to the server before shutting down the client. 10/11/2022, Bing Li
				 */
				try
				{
					if (!client.isInputStreamValid())
					{
						client.setInputStream();
					}
					client.sendWithResponse(new LeaveRequest());
					this.disposer.dispose(client);
				}
				catch (ClassNotFoundException | IOException e)
				{
					/*
					 * Since the client needs to shut down, the exception does not need to take care. 10/12/2022, Bing Li
					 */
					log.info("Server is down");
				}
			}
		}
		this.busyMap.clear();

		// Dispose all of the idle FreeClients. It might lose data if no relevant management approaches are adopted. 11/06/2014, Bing Li
		for (Map<String, FreeClient> clients : this.idleMap.values())
		{
			for (FreeClient client : clients.values())
			{
				/*
				 * The line delays some time to ensure previous data is sent out to the server before shutting down the client. 10/11/2022, Bing Li
				 */
				try
				{
					if (!client.isInputStreamValid())
					{
						client.setInputStream();
					}
					client.sendWithResponse(new LeaveRequest());
					this.disposer.dispose(client);
				}
				catch (ClassNotFoundException | IOException e)
				{
					/*
					 * Since the client needs to shut down, the exception does not need to take care. 10/12/2022, Bing Li
					 */
					log.info("Server is down");
				}
			}
		}
		this.idleMap.clear();
		
		// Clear the sources. 11/06/2014, Bing Li
		this.resourceMap.clear();
		
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
		this.idleChecker = new ClientManagerIdleChecker(this);
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
		
//		log.info("Idle clients' size = " + this.idleMap.size());

		// The map is used to keep the sorted instances of FreeClient to select the longest idle one conveniently. 11/06/2014, Bing Li
		Map<String, FreeClient> sortedResourceMap;

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
			// Notify the thread that is blocked for the maximum size of the pool is reached. 11/06/2014, Bing Li
			this.collaborator.signal();
		}
	}
	
	/*
	 * Remove the type of FreeClient explicitly by the key of the type. It is used in the case when a thread confirms that one type of FreeClient is not needed to be created in the pool. It is not used frequently. Just keep an interface for possible cases. 11/06/2014, Bing Li
	 */
	public void removeClient(String objectKey) throws IOException
	{
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
		// Notify all of the threads that are waiting for FreeClient to keep on working if instances of FreeClient are available for the removal from the idle map. 11/06/2014, Bing Li
		this.collaborator.signalAll();
	}
	
	public int getClientSize()
	{
		return this.busyMap.size() + this.idleMap.size();
	}

	/*
	 * Check whether a specific instance of FreeClient is busy. 11/06/2014, Bing Li
	 */
	public boolean isBusy(FreeClient client)
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
	
	/*
	 * Return all of the type keys of FreeClient. 11/06/2014, Bing Li
	 */
	public Set<String> getAllObjectKeys()
	{
		return this.resourceMap.keySet();
	}
	
	/*
	 * Get the IPPort of a particular type of FreeClient. 11/06/2014, Bing Li
	 */
	public IPResource getIPResource(String objectKey)
	{
		// Check whether the source is available in the source map. 11/06/2014, Bing Li
		if (this.resourceMap.containsKey(objectKey))
		{
			// Return the source if it is existed in the source map. 11/06/2014, Bing Li
			return this.resourceMap.get(objectKey);
		}
		// Return null if the source is not available in the source map. 11/06/2014, Bing Li
		return null;
	}
	
	public void removeIPResource(String objectKey)
	{
		if (this.resourceMap.containsKey(objectKey))
		{
//			log.info(objectKey + " is being removed!");
			this.resourceMap.remove(objectKey);
		}
		else
		{
//			log.info(objectKey + " does not exist ...");
		}
	}
	
	/*
	 * Get the size of IPPorts. 11/06/2014, Bing Li
	 */
	public int getSourceSize()
	{
		return this.resourceMap.size();
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
		return this.resourceMap.containsKey(key);
	}
	
	/*
	 * Check whether a particular IPPort is available by the IPPort itself. 11/06/2014, Bing Li
	 */
	public boolean isSourceExisted(IPResource source)
	{
		return this.resourceMap.containsKey(source.getObjectKey());
	}

	public boolean isResourceExisted(String key)
	{
		return this.busyMap.containsKey(key) || this.idleMap.containsKey(key);
	}
	
	/*
	 * Get the size of all of the types of busy instances of FreeClient at the moment when the method is invoked. 11/06/2014, Bing Li
	 */
	public int getBusyClientSize()
	{
		int busyClientCount = 0;
		for (Map<String, FreeClient> rscs : this.busyMap.values())
		{
			busyClientCount += rscs.size();
		}
		return busyClientCount;
	}
	
	/*
	 * Get the size of all of the types of idle instances of FreeClient at the moment when the method is invoked. 11/06/2014, Bing Li
	 */
	public int getIdleClientSize()
	{
		int idleClientCount = 0;
		for (Map<String, FreeClient> rscs : this.idleMap.values())
		{
			idleClientCount += rscs.size();
		}
		return idleClientCount;
	}

	/*
	 * After receiving feedback from the remote server, the method is called to notify the relevant to complete the initialization of ObjectInputStream of the FreeClient instance. 11/06/2014, Bing Li 
	 */
	/*
	public void notifyOutStreamDone()
	{
		this.initReadCollaborator.signal();
	}
	*/

	public void addIPResource(IPResource ipRsc)
	{
		this.resourceMap.put(ipRsc.getObjectKey(), ipRsc);
	}

	public void clear()
	{
		this.resourceMap.clear();
		this.busyMap.clear();
		this.idleMap.clear();
	}

	public synchronized FreeClient get(IPResource ipRsc, boolean isRead) throws IOException
	{
		// The resource to be returned. 09/17/2014, Bing Li
		FreeClient client = null;
		// One particular type of resources that is the same type as the requested one. 09/17/2014, Bing Li
		Map<String, FreeClient> clients;
		// The hash key of the resource that has the longest lifetime. 09/17/2014, Bing Li
		String mostIdleClientKey;
		// The count of busy resources. 09/17/2014, Bing Li
		int busyClientCount;
		// The count of idle resources 09/17/2014, Bing Li
		int idleClientCount;

		// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 09/17/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
			busyClientCount = 0;
			// Initialize the value of idleResourceCount. 09/17/2014, Bing Li
			idleClientCount = 0;
			
			/*
			 * Retrieve the resource from the idle map first. 09/17/2014, Bing Li
			 */

			// Check whether the idle map contains the type of the resource. 09/17/2014, Bing Li
			if (this.idleMap.containsKey(ipRsc.getObjectKey()))
			{
				// The type of idle resources which are the candidates to be returned. 09/17/2014, Bing Li
				clients = this.idleMap.get(ipRsc.getObjectKey());
				// Get the hash key of the resource that is idle longer than any others from the idle resources. 09/17/2014, Bing Li 
				mostIdleClientKey = CollectionSorter.minValueKey(clients);
				// Check whether the hash key is valid. 09/17/2014, Bing Li
				if (mostIdleClientKey != null)
				{
					// Get the resource that is idle than any others by its hash key. 09/17/2014, Bing Li
					client = clients.get(mostIdleClientKey);
					// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
					client.setAccessedTime();
					// Remove the resource from the idle map since it will become a busy one. 09/17/2014, Bing Li
					this.idleMap.get(ipRsc.getObjectKey()).remove(mostIdleClientKey);
					// Check whether the type of resources is empty in the idle map. 09/17/2014, Bing Li
					if (this.idleMap.get(ipRsc.getObjectKey()).size() <= 0)
					{
						// Remove the type of resources from the idle map if no any specific resources of the type is in the idle map. 09/17/2014, Bing Li
						this.idleMap.remove(ipRsc.getObjectKey());
					}
					// Check whether the type of resources is available in the busy map. 09/17/2014, Bing Li
					if (!this.busyMap.containsKey(client.getObjectKey()))
					{
						// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
						this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
						this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
					}
					else
					{
						// If the type of resources is existed in the busy map, add the particular resource. 09/17/2014, Bing Li
						this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
					}
				}
			}

			/*
			 * If the idle map does not contain the requested resource, it is necessary to initialize a new one. 09/17/2014, Bing Li
			 */

			// Check whether the resource is available after retrieving from the idle map. 10/12/2014, Bing Li
			if (client == null)
			{
				// Calculate the exact count of all of the busy resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
				for (Map<String, FreeClient> rscs : this.busyMap.values())
				{
					busyClientCount += rscs.size();
				}
				
				// Calculate the exact count of all of the idle resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
				for (Map<String, FreeClient> rscs : this.idleMap.values())
				{
					idleClientCount += rscs.size();
				}
				
				// Check whether the sum of the count of busy and idle resources reach the upper limit of the pool. 09/17/2014, Bing Li 
				if (busyClientCount + idleClientCount < this.poolSize)
				{
					// If the upper limit of the pool is not reached, it is time to create an instance by its source. 09/17/2014, Bing Li
					client = this.creator.createClientInstance(ipRsc);
					// Check whether the newly created instance is valid. 09/17/2014, Bing Li
					if (client != null)
					{
						// Check whether the type of the resource is available in the busy map. 09/17/2014, Bing Li
						if (!this.busyMap.containsKey(client.getObjectKey()))
						{
							// If the type of the resource is not available in the busy map, add the type and the resource into it. 09/17/2014, Bing Li
							this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
							this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
						}
						else
						{
							// If the type of the resource is available in the busy map, add the resource into it. 09/17/2014, Bing Li
							this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
						}
						// Add the source to the source map. 09/17/2014, Bing Li
						this.resourceMap.put(client.getObjectKey(), ipRsc);
					}
					if (isRead)
					{
						/*
						 * It seems that the below two lines of code are not needed. 10/15/2022, Bing Li
						 */
						// Return the resource to the thread. 09/17/2014, Bing Li
//						client.initRead();
						// Wait for feedback from the remote server. 11/06/2014, Bing Li
//						this.initReadCollaborator.holdOn(UtilConfig.INIT_READ_WAIT_TIME);
						// After the feedback is received, the ObjectInputStream can be initialized. 11/06/2014, Bing Li
						client.setInputStream();
					}
//					log.info("Client (" + client + ") is created ... isRead = " + isRead);
					return client;
				}
			}
			else
			{
//				log.info("Client (" + client + ") is reused ... isRead = " + isRead);
				if (!client.isInputStreamValid() && isRead)
				{
					client.setInputStream();
				}
				// If the resource is obtained from the idle map and then return the resource to the thread. 09/17/2014, Bing Li
				return client;
			}
			
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
	public FreeClient get(String objectKey, boolean isRead) throws IOException
	{
		// Check whether the key is valid. 09/17/2014, Bing Li
		if (objectKey != UtilConfig.NO_KEY)
		{
			IPResource ipRsc;
			// The resource to be returned. 09/17/2014, Bing Li
			FreeClient client = null;
			// One particular type of resources that is the same type as the requested one. 09/17/2014, Bing Li
			Map<String, FreeClient> clients;
			// The hash key of the resource that has the longest lifetime. 09/17/2014, Bing Li
			String mostIdleClientKey;
			// The count of busy resources. 09/17/2014, Bing Li
			int busyClientCount;
			// The count of idle resources 09/17/2014, Bing Li
			int idleClientCount;
			boolean isClientAvailable = false;

			// Since the procedure to get a particular resource might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 09/17/2014, Bing Li
			while (!this.collaborator.isShutdown())
			{
				// Check whether the type of the resource is existed in the source map. 09/17/2014, Bing Li
				if (this.resourceMap.containsKey(objectKey))
				{
					// Set the flag that the source is available. 11/03/2014, Bing Li
					isClientAvailable = true;
					// Retrieve the source. 11/03/2014, Bing Li
					ipRsc = this.resourceMap.get(objectKey);
					// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
					busyClientCount = 0;
					// Initialize the value of busyResourceCount. 09/17/2014, Bing Li
					idleClientCount = 0;

					/*
					 * Retrieve the resource from the idle map first. 09/17/2014, Bing Li
					 */

					// Check whether the idle map contains the type of the resource. 09/17/2014, Bing Li
					if (this.idleMap.containsKey(ipRsc.getObjectKey()))
					{
						// The type of idle resources which are the candidates to be returned. 09/17/2014, Bing Li
						clients = this.idleMap.get(ipRsc.getObjectKey());
						// Get the hash key of the resource that is idle longer than any others from the idle resources. 09/17/2014, Bing Li 
						mostIdleClientKey = CollectionSorter.minValueKey(clients);
						// Check whether the hash key is valid. 09/17/2014, Bing Li
						if (mostIdleClientKey != null)
						{
							// Get the resource that is idle than any others by its hash key. 09/17/2014, Bing Li
							client = clients.get(mostIdleClientKey);

							// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
							client.setAccessedTime();
							
							// Set the accessed time of the resource that is idle than any others to the current moment since the resource will be used after it is returned. Now it becomes a busy one. 09/17/2014, Bing Li 
							this.idleMap.get(ipRsc.getObjectKey()).remove(mostIdleClientKey);
							// Check whether the type of resources is empty in the idle map. 09/17/2014, Bing Li
							if (this.idleMap.get(ipRsc.getObjectKey()).size() <= 0)
							{
								// Remove the type of resources from the idle map if no any specific resources of the type is in the idle map. 09/17/2014, Bing Li
								this.idleMap.remove(ipRsc.getObjectKey());
							}

							// Check whether the type of resources is available in the busy map. 09/17/2014, Bing Li
							if (!this.busyMap.containsKey(client.getObjectKey()))
							{
								// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
								this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
								this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
							}
							else
							{
								// If the type of resources is not existed in the busy map, add the type and the particular resource. 09/17/2014, Bing Li
								this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
							}
						}
					}

					/*
					 * If the idle map does not contain the requested resource, it is necessary to initialize a new one. 09/17/2014, Bing Li
					 */

					// Check whether the resource is available when retrieving from the idle map. 10/12/2014, Bing Li
					if (client == null)
					{
						// Calculate the exact count of all of the busy resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
						for (Map<String, FreeClient> rscs : this.busyMap.values())
						{
							busyClientCount += rscs.size();
						}

						// Calculate the exact count of all of the idle resources. The value is used to check whether the upper limit of the pool is reached. 09/17/2014, Bing Li
						for (Map<String, FreeClient> rscs : this.idleMap.values())
						{
							idleClientCount += rscs.size();
						}

						// Check whether the sum of the count of busy and idle resources reach the upper limit of the pool. 09/17/2014, Bing Li 
						if (busyClientCount + idleClientCount < this.poolSize)
						{
							// If the upper limit of the pool is not reached, it is time to create an instance by its source. 09/17/2014, Bing Li
							client = this.creator.createClientInstance(ipRsc);
							// Check whether the newly created instance is valid. 09/17/2014, Bing Li
							if (client != null)
							{
								// Check whether the type of the resource is available in the busy map. 09/17/2014, Bing Li
								if (!this.busyMap.containsKey(client.getObjectKey()))
								{
									// If the type of the resource is not available in the busy map, add the type and the resource into it. 09/17/2014, Bing Li
									this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
									this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
								}
								else
								{
									// If the type of the resource is available in the busy map, add the resource into it. 09/17/2014, Bing Li
									this.busyMap.get(client.getObjectKey()).put(client.getHashKey(), client);
								}
							}
							if (isRead)
							{
								/*
								 * It seems that the below two lines of code are not needed. 10/15/2022, Bing Li
								 */
//								client.initRead();
								// Wait for feedback from the remote server. 11/06/2014, Bing Li
//								this.initReadCollaborator.holdOn(UtilConfig.INIT_READ_WAIT_TIME);
								// After the feedback is received, the ObjectInputStream can be initialized. 11/06/2014, Bing Li
								client.setInputStream();
							}
//							log.info("Client (" + client + ") is created ... isRead = " + isRead);
							// Return the resource to the invoker. 09/17/2014, Bing Li
							return client;
						}
					}
					else
					{
//						log.info("Client (" + client + ") is reused ... isRead = " + isRead);
						if (!client.isInputStreamValid() && isRead)
						{
							client.setInputStream();
						}
					}
					// Return the instance of FreeClient to the thread. 11/06/2014, Bing Li
					return client;
				}

				// If the source is available, it is expected to wait for idle resource. 11/03/2014, Bing Li
				if (isClientAvailable)
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

	/*
	 * Get the instance of FreeClient by the IP/port of the remote server. The argument, nodeKey, is used to notify the remote server to retrieve the client to the local end such that the feedback can be sent. 11/06/2014, Bing Li
	 */
	/*
	public FreeClient get(String clientKey, IPResource ipRsc) throws IOException
	{
		// The instance of FreeClient to be returned. 11/06/2014, Bing Li
		FreeClient client = null;
		// One particular type of FreeClient that is the same type as the requested one. 11/06/2014, Bing Li
		Map<String, FreeClient> clients;
		// The hash key of the FreeClient instance that has the longest lifetime. 11/06/2014, Bing Li
		String mostIdleClientKey;
		// The count of busy instances of FreeClient. 11/06/2014, Bing Li
		int busyClientCount;
		// The count of idle instances of FreeClient. 11/06/2014, Bing Li
		int idleClientCount;

		// Since the procedure to get an instance of FreeClient might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 11/06/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			// Initialize the value of busyResourceCount. 11/06/2014, Bing Li
			busyClientCount = 0;
			// Initialize the value of idleResourceCount. 11/06/2014, Bing Li
			idleClientCount = 0;

			// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
			if (this.idleMap.containsKey(ipRsc.getObjectKey()))
			{
				// The type of idle FreeClient instances which are the candidates to be returned. 11/06/2014, Bing Li
				clients = this.idleMap.get(ipRsc.getObjectKey());
				// Get the hash key of the client that is idle longer than any others from the idle FreeClient instances. 11/06/2014, Bing Li 
				mostIdleClientKey = CollectionSorter.minValueKey(clients);
				// Check whether the hash key is valid. 11/06/2014, Bing Li
				if (mostIdleClientKey != null)
				{
					// Get the instance of FreeClient that is idle than any others by its hash key. 11/06/2014, Bing Li
					client = clients.get(mostIdleClientKey);
					// Set the accessed time of the client that is idle than any others to the current moment since the client will be used after it is returned. Now it becomes a busy one. 11/06/2014, Bing Li
					client.setAccessedTime();
					// Remove the instance of FreeClient from the idle map since it will become a busy one. 11/06/2014, Bing Li
					this.idleMap.get(ipRsc.getObjectKey()).remove(mostIdleClientKey);
					// Check whether the type of FreeClient is empty in the idle map. 11/06/2014, Bing Li
					if (this.idleMap.get(ipRsc.getObjectKey()).size() <= 0)
					{
						// Remove the type of FreeClient from the idle map if no any specific instances of the type is in the idle map. 11/06/2014, Bing Li
						this.idleMap.remove(ipRsc.getObjectKey());
					}
					// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
					if (!this.busyMap.containsKey(client.getObjectKey()))
					{
						// If the type of FreeClient is not existed in the busy map, add the type and the particular instance. 11/06/2014, Bing Li
						this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
						this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
					}
					else
					{
						// If the type of FreeClient is existed in the busy map, add the particular instance. 11/06/2014, Bing Li
						this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
					}
				}
			}

			// Check whether the instance of FreeClient is available after retrieving from the idle map. 11/06/2014, Bing Li
			if (client == null)
			{
				// Calculate the exact count of all of the busy instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
				for (Map<String, FreeClient> rscs : this.busyMap.values())
				{
					busyClientCount += rscs.size();
				}

				// Calculate the exact count of all of the idle instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
				for (Map<String, FreeClient> rscs : this.idleMap.values())
				{
					idleClientCount += rscs.size();
				}

				// Check whether the sum of the count of busy and idle instances of FreeClient reach the upper limit of the pool. 11/06/2014, Bing Li 
				if (busyClientCount + idleClientCount < this.poolSize)
				{
					// If the upper limit of the pool is not reached, it is time to create an instance of FreeClient by its IPPort. 11/06/2014, Bing Li
					client = this.creator.createClientInstance(ipRsc);
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
						this.resourceMap.put(client.getObjectKey(), ipRsc);
					}

					// The line sends a message to the remote server. If it receives the message and send a feedback, it indicates that the ObjectOutputStream is initialized on the server. Then, the local ObjectInputStream can be initialized. It avoids the possible getting blocked. 11/06/2014, Bing Li
//					client.initRead(clientKey);
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
				if (!client.isInputStreamValid())
				{
					client.setInputStream();
				}
				// If the instance of FreeClient is obtained from the idle map and then return it to the thread. 11/06/2014, Bing Li
				return client;
			}

			// Check whether the pool is shutdown. 11/06/2014, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// If the pool is not shutdown, it is time to wait for some time. After that, the above procedure is repeated if the pool is not shutdown. 11/06/2014, Bing Li
				this.collaborator.holdOn(UtilConfig.ONE_SECOND);
			}
		}
		return null;
	}
	*/

	/*
	 * Get the instance of FreeClient by the key of IPPort of the remote server. The argument, nodeKey, is used to notify the remote server to retrieve the client to the local end such that the feedback can be sent. 11/06/2014, Bing Li
	 */
	/*
	public FreeClient get(String clientKey, String objectKey) throws IOException
	{
		// Check whether the key is valid. 11/06/2014, Bing Li
		if (objectKey != UtilConfig.NO_KEY)
		{
			// Declare an instance of IPPort. 11/06/2014, Bing Li
			IPResource ipRsc;
			// The instance of FreeClient to be returned. 11/06/2014, Bing Li
			FreeClient client = null;
			// One particular type of FreeClient that is the same type as the requested one. 11/06/2014, Bing Li
			Map<String, FreeClient> clients;
			// The hash key of the FreeClient instance that has the longest lifetime. 11/06/2014, Bing Li
			String mostIdleClientKey;
			// The count of busy instances of FreeClient. 11/06/2014, Bing Li
			int busyClientCount;
			// The count of idle instances of FreeClient. 11/06/2014, Bing Li
			int idleClientCount;

			// A flag to indicate whether the relevant IPPort is available. 11/06/2014, Bing Li
			boolean isClientAvailable = false;
			// Since the procedure to get an instance of FreeClient might be blocked when the upper limit of the pool is reached, it is required to keep a notify/wait mechanism to guarantee the procedure smooth. 11/06/2014, Bing Li
			while (!this.collaborator.isShutdown())
			{
				// Check if the IPPort is available. 11/06/2014, Bing Li
				if (this.resourceMap.containsKey(objectKey))
				{
					// Set the flag that the IPPort is available. 11/06/2014, Bing Li
					isClientAvailable = true;
					// Retrieve the IPPort. 11/06/2014, Bing Li
					ipRsc = this.resourceMap.get(objectKey);
					// Initialize the value of busyResourceCount. 11/06/2014, Bing Li
					busyClientCount = 0;
					// Initialize the value of idleResourceCount. 11/06/2014, Bing Li
					idleClientCount = 0;

					// Check whether the idle map contains the type of FreeClient. 11/06/2014, Bing Li
					if (this.idleMap.containsKey(ipRsc.getObjectKey()))
					{
						// The type of idle FreeClient instances which are the candidates to be returned. 11/06/2014, Bing Li
						clients = this.idleMap.get(ipRsc.getObjectKey());
						// Get the hash key of the client that is idle longer than any others from the idle FreeClient instances. 11/06/2014, Bing Li 
						mostIdleClientKey = CollectionSorter.minValueKey(clients);
						// Check whether the hash key is valid. 11/06/2014, Bing Li
						if (mostIdleClientKey != null)
						{
							// Get the instance of FreeClient that is idle than any others by its hash key. 11/06/2014, Bing Li
							client = clients.get(mostIdleClientKey);
							// Set the accessed time of the client that is idle than any others to the current moment since the client will be used after it is returned. Now it becomes a busy one. 11/06/2014, Bing Li
							client.setAccessedTime();
							// Remove the instance of FreeClient from the idle map since it will become a busy one. 11/06/2014, Bing Li
							this.idleMap.get(ipRsc.getObjectKey()).remove(mostIdleClientKey);
							// Check whether the type of FreeClient is empty in the idle map. 11/06/2014, Bing Li
							if (this.idleMap.get(ipRsc.getObjectKey()).size() <= 0)
							{
								// Remove the type of FreeClient from the idle map if no any specific instances of the type is in the idle map. 11/06/2014, Bing Li
								this.idleMap.remove(ipRsc.getObjectKey());
							}
							// Check whether the type of FreeClient is available in the busy map. 11/06/2014, Bing Li
							if (!this.busyMap.containsKey(client.getObjectKey()))
							{
								// If the type of FreeClient is not existed in the busy map, add the type and the particular instance. 11/06/2014, Bing Li
								this.busyMap.put(client.getObjectKey(), new ConcurrentHashMap<String, FreeClient>());
								this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
							}
							else
							{
								// If the type of FreeClient is existed in the busy map, add the particular instance. 11/06/2014, Bing Li
								this.busyMap.get(client.getObjectKey()).put(mostIdleClientKey, client);
							}
						}
					}

					// Check whether the instance of FreeClient is available after retrieving from the idle map. 11/06/2014, Bing Li
					if (client == null)
					{
						// Calculate the exact count of all of the busy instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
						for (Map<String, FreeClient> rscs : this.busyMap.values())
						{
							busyClientCount += rscs.size();
						}

						// Calculate the exact count of all of the idle instances of FreeClient. The value is used to check whether the upper limit of the pool is reached. 11/06/2014, Bing Li
						for (Map<String, FreeClient> rscs : this.idleMap.values())
						{
							idleClientCount += rscs.size();
						}

						// Check whether the sum of the count of busy and idle instances of FreeClient reach the upper limit of the pool. 11/06/2014, Bing Li 
						if (busyClientCount + idleClientCount < this.poolSize)
						{
							// If the upper limit of the pool is not reached, it is time to create an instance of FreeClient by its IPPort. 11/06/2014, Bing Li
							client = this.creator.createClientInstance(ipRsc);
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
//							client.initRead(clientKey);
							client.initRead();
							// Wait for feedback from the remote server. 11/06/2014, Bing Li
							this.initReadCollaborator.holdOn(UtilConfig.INIT_READ_WAIT_TIME);
							// After the feedback is received, the ObjectInputStream can be initialized. 11/06/2014, Bing Li
							client.setInputStream();
						}
					}
					else
					{
						if (!client.isInputStreamValid())
						{
							client.setInputStream();
						}
					}
					// Return the instance of FreeClient to the thread. 11/06/2014, Bing Li
					return client;
				}

				// Check if the IPPort is available. 11/06/2014, Bing Li
				if (isClientAvailable)
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
	*/

}
