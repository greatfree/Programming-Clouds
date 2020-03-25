package org.greatfree.abandoned.cache.distributed.child.update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 07/17/2017, Bing Li
public class ChildMapRegistry
{
	private Map<String, DistributedPersistableChildMap> distributedMaps;
	
	private ChildMapRegistry()
	{
	}
	
	private static ChildMapRegistry instance = new ChildMapRegistry();
	
	public static ChildMapRegistry CACHE()
	{
		if (instance == null)
		{
			instance = new ChildMapRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.distributedMaps.clear();
		this.distributedMaps = null;
	}
	
	public void init()
	{
		this.distributedMaps = new ConcurrentHashMap<String, DistributedPersistableChildMap>();
	}
	
	public void register(DistributedPersistableChildMap map)
	{
		if (!this.distributedMaps.containsKey(map.getCacheKey()))
		{
			this.distributedMaps.put(map.getCacheKey(), map);
		}
	}
	
	public DistributedPersistableChildMap getMap(String cacheKey)
	{
		if (this.distributedMaps.containsKey(cacheKey))
		{
			return this.distributedMaps.get(cacheKey);
		}
		return null;
	}
	
	public void unregister(DistributedPersistableChildMap map)
	{
		this.distributedMaps.remove(map.getCacheKey());
	}
}
