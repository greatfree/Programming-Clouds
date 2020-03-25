package org.greatfree.abandoned.cache.distributed.root;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;

/*
 * The registry keeps all of the created instances of caches according to their keys such that each cache can be retrieved by the cache cluster root. 07/03/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
//public class MapRegistry<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
public class MapRegistry<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
{
//	private Map<String, DistributedPersistableMap<Value, Factory, DB>> distributedMaps;
	private Map<String, DistributedPersistableMap<Key, Value, Factory, DB>> distributedMaps;
	
	public MapRegistry()
	{
//		this.distributedMaps = new ConcurrentHashMap<String, DistributedPersistableMap<Value, Factory, DB>>();
		this.distributedMaps = new ConcurrentHashMap<String, DistributedPersistableMap<Key, Value, Factory, DB>>();
	}
	
	public void dispose()
	{
		this.distributedMaps.clear();
		this.distributedMaps = null;
	}

//	public void put(String key, DistributedPersistableMap<Value, Factory, DB> cache)
//	public void register(DistributedPersistableMap<Value, Factory, DB> cache)
	public void register(DistributedPersistableMap<Key, Value, Factory, DB> cache)
	{
		/*
		if (!this.distributedMaps.containsKey(key))
		{
			this.distributedMaps.put(key, cache);
		}
		*/
		if (!this.distributedMaps.containsKey(cache.getCacheKey()))
		{
			this.distributedMaps.put(cache.getCacheKey(), cache);
		}
	}

//	public DistributedPersistableMap<Value, Factory, DB> get(String key)
	public DistributedPersistableMap<Key, Value, Factory, DB> get(String key)
	{
		return this.distributedMaps.get(key);
	}
	
//	public void remove(String key)
	public void unregister(String key)
	{
		this.distributedMaps.remove(key);
	}
}
