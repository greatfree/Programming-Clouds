package org.greatfree.reuse;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FreeObject;

/*
 * This class is a cache to save the resources that are used in a high probability. It is designed since the total amount of data is too large to be loaded into the memory. Therefore, only the ones that are used frequently are loaded into the cache. It is possible that some loaded ones are obsolete. It is necessary to load new ones that are used frequently into the cached and save the ones that are out of date into the database or the file system persistently. 09/22/2014, Bing Li
 */

// Created: 09/22/2014, Bing Li
public class ResourceCache<Resource extends FreeObject>
{
	// The size of the cache. 09/22/2014, Bing Li
	private long cacheSize;
	// The collection that keeps all of the resource in the cache. 09/22/2014, Bing Li
	private Map<String, Resource> resourceMap;
	// The lock is used to keep thread-safe. 11/28/2014, Bing Li
	private ReentrantReadWriteLock lock;
	
	/*
	 * Initialize the cache. 09/22/2014, Bing Li
	 */
	public ResourceCache(long cacheSize)
	{
		this.cacheSize = cacheSize;
		this.resourceMap = new ConcurrentHashMap<String, Resource>();
		this.lock = new ReentrantReadWriteLock();
	}

	/*
	 * Dispose the cache. 09/22/2014, Bing Li
	 */
	public void dispose()
	{
		if (this.resourceMap != null)
		{
			this.resourceMap.clear();
		}
	}

	/*
	 * Clear the cache. 09/22/2014, Bing Li
	 */
	public void clear()
	{
		this.resourceMap.clear();
	}

	/*
	 * Reset the cache size. 09/22/2014, Bing Li
	 */
	public synchronized void resetCacheSize(long cacheSize)
	{
		this.cacheSize = cacheSize;
	}

	/*
	 * Get the cache size. 09/22/2014, Bing Li
	 */
	public synchronized long getCacheSize()
	{
		return this.cacheSize;
	}

	/*
	 * Assign resources to the cache. All of the existing resources are cleared and the ones to be set are put into the cache. 09/22/2014, Bing Li 
	 */
	public void setResources(Map<String, Resource> resourceMap)
	{
		// Clear the existing resources. 09/22/2014, Bing Li
		this.clear();
		// Add the resources to the cache. 09/22/2014, Bing Li
		for (Resource resource : resourceMap.values())
		{
			// Add one piece of resource. 11/28/2014, Bing Li
			this.addResource(resource);
		}
	}

	/*
	 * Add a new resource into the cache. 09/22/2014, Bing Li
	 */
	public Resource addResource(Resource resource)
	{
		// It is possible that the cache is full such that an obsolete one must be removed. The instance is declared here. 09/22/2014, Bing Li
		Resource removedRsc = null;
		// Keep thread safe. 09/22/2014, Bing Li
		this.lock.readLock().lock();
		// Check whether the size of the current resource exceeds the upper limit. 11/28/2014, Bing Li
		if (this.resourceMap.size() >= this.cacheSize)
		{
			// Selected the one that is accessed with the least probability. 11/28/2014, Bing Li
			String leastAccessedKey = CollectionSorter.minValueKey(this.resourceMap);
			// Check whether the key is valid. 11/28/2014, Bing Li
			if (leastAccessedKey != null)
			{
				// Get the resource that is accessed with the least probability. 11/28/2014, Bing Li
				removedRsc = this.resourceMap.get(leastAccessedKey);
				this.lock.readLock().unlock();
				this.lock.writeLock().lock();
				// Remove the resource from the cache. 11/28/2014, Bing Li
				this.resourceMap.remove(leastAccessedKey);
				this.lock.readLock().lock();
				this.lock.writeLock().unlock();
			}
		}
		this.lock.readLock().unlock();
		
		this.lock.writeLock().lock();
		// Put the new resource into the cache. 11/28/2014, Bing Li
		this.resourceMap.put(resource.getObjectKey(), resource);
		// Set the time stamp to access the new resource. 11/28/2014, Bing Li
		this.resourceMap.get(resource.getObjectKey()).setAccessedTime();
		this.lock.writeLock().unlock();
		// Return the removed resource for possible further processing. 11/28/2014, Bing Li
		return removedRsc;
	}

	/*
	 * Get one specific resource from the cache by its key. 11/28/2014, Bing Li
	 */
	public Resource getResource(String resourceKey)
	{
		this.lock.readLock().lock();
		try
		{
			// Check whether the resource exists in the cache by its key. 11/28/2014, Bing Li
			if (this.resourceMap.containsKey(resourceKey))
			{
				// If the resource exists, set the accessed time stamp. 11/28/2014, Bing Li
				this.resourceMap.get(resourceKey).setAccessedTime();
				// Return the resource. 11/28/2014, Bing Li
				return this.resourceMap.get(resourceKey);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		// Return null if the key does not exist. 11/28/2014, Bing Li
		return null;
	}

	/*
	 * Check whether one specific resource exists in the cache by its key. 11/28/2014, Bing Li
	 */
	public boolean isResourceAvailable(String resourceKey)
	{
		this.lock.readLock().lock();
		try
		{
			// Check whether the resource exists in the cache by its key. 11/28/2014, Bing Li
			if (this.resourceMap.containsKey(resourceKey))
			{
				// If the resource exists, set the accessed time stamp. 11/28/2014, Bing Li
				this.resourceMap.get(resourceKey).setAccessedTime();
				// Return true if the key exists. 11/28/2014, Bing Li
				return true;
			}
			else
			{
				// Return false if the key exists. 11/28/2014, Bing Li
				return false;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}

	/*
	 * Remove resources from the cache by their keys. 11/28/2014, Bing Li
	 */
	public void removeResources(Set<String> resourceKeys)
	{
		// Scan each key. 11/28/2014, Bing Li
		for (String resourceKey : resourceKeys)
		{
			// Remove the resource by its key. 11/28/2014, Bing Li
			this.removeResource(resourceKey);
		}
	}

	/*
	 * Remove one particular resource from the cache by its key. 11/28/2014, Bing Li
	 */
	public void removeResource(String resourceKey)
	{
		this.lock.readLock().lock();
		// Check whether the key of the resource exists. 11/28/2014, Bing Li
		if (this.resourceMap.containsKey(resourceKey))
		{
			this.lock.readLock().unlock();
			this.lock.writeLock().lock();
			// Remove the resource from the cache. 11/28/2014, Bing Li
			this.resourceMap.remove(resourceKey);
			this.lock.readLock().lock();
			this.lock.writeLock().unlock();
		}
		this.lock.readLock().unlock();
	}

	/*
	 * Get all of the resources in the cache. 11/28/2014, Bing Li
	 */
	public Map<String, Resource> getResources()
	{
		return this.resourceMap;
	}

	/*
	 * Get the keys of all of the resource keys in the cache. 11/28/2014, Bing Li
	 */
	public Set<String> getResourceKeys()
	{
		return this.resourceMap.keySet();
	}

	/*
	 * Check whether the cache is empty or not. 11/28/2014, Bing Li
	 */
	public boolean isEmpty()
	{
		return this.resourceMap.size() <= 0;
	}

	/*
	 * Check whether the cache is full or not. 11/28/2014, Bing Li
	 */
	public boolean isFull()
	{
		return this.resourceMap.size() >= this.cacheSize;
	}

	/*
	 * Get the size of available resources in the cache. 11/28/2014, Bing Li
	 */
	public int getResourceSize()
	{
		return this.resourceMap.size();
	}
}
