package org.greatfree.abandoned.cache.distributed.root.update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 07/17/2017, Bing Li
public class MapRegistry
{
	private Map<String, DistributedPersistableMap> distributedMaps;
	
	private MapRegistry()
	{
	}
	
	private static MapRegistry instance = new MapRegistry();
	
	public static MapRegistry CACHE()
	{
		if (instance == null)
		{
			instance = new MapRegistry();
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
		this.distributedMaps = new ConcurrentHashMap<String, DistributedPersistableMap>();
	}
	
	public void register(DistributedPersistableMap map)
	{
		if (!this.distributedMaps.containsKey(map.getCacheKey()))
		{
			this.distributedMaps.put(map.getCacheKey(), map);
		}
	}
	
	public DistributedPersistableMap getMap(String cacheKey)
	{
		if (this.distributedMaps.containsKey(cacheKey))
		{
			return this.distributedMaps.get(cacheKey);
		}
		return null;
	}
	
	public void unregister(DistributedPersistableMap map)
	{
		this.distributedMaps.remove(map.getCacheKey());
	}
}
