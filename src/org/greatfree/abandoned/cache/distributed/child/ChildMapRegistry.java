package org.greatfree.abandoned.cache.distributed.child;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;

/*
 * The registry keeps all of instances of local distributed child maps at a specific node. When data is received, one particular instance can be retrieved from the registry such that received data can be saved in the child map. 07/20/2017, Bing Li
 */

// Created: 07/15/2017, Bing Li
//public class ChildMapRegistry<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
public class ChildMapRegistry<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
{
	// All of the instances of children distributed maps in the local node is retained in the map. Each instance of children distributed map has one unique root distributed map. The map keeps the corresponding relationship. So the key is the one of the root distributed map. 07/20/2017, Bing Li
	private Map<String, DistributedPersistableChildMap<Key, Value, Factory, DB>> distributedMaps;
	// Each instance of children distributed map has one unique root distributed map. The map keeps the corresponding relationship. 07/20/2017, Bing Li
//	private Map<String, String> joinedMaps;

	/*
	 * Initialize the registry. 07/20/2017, Bing Li
	 */
	public ChildMapRegistry()
	{
		this.distributedMaps = new ConcurrentHashMap<String, DistributedPersistableChildMap<Key, Value, Factory, DB>>();
//		this.joinedMaps = new ConcurrentHashMap<String, String>();
	}

	/*
	 * Dispose the registry. 07/20/2017, Bing Li
	 */
	public void dispose()
	{
		this.distributedMaps.clear();
		this.distributedMaps = null;
		
//		this.joinedMaps.clear();
//		this.joinedMaps = null;
	}

	
//	public void register(String key, DistributedPersistableChildMap<Value, Factory, DB> cache)
//	public void register(DistributedPersistableChildMap<Value, Factory, DB> cache)
	public void register(String rootCacheKey, DistributedPersistableChildMap<Key, Value, Factory, DB> cache)
	{
		/*
		if (!this.distributedMaps.containsKey(key))
		{
			this.distributedMaps.put(key, cache);
		}
		*/
		/*
		if (!this.distributedMaps.containsKey(cache.getCacheKey()))
		{
			this.distributedMaps.put(cache.getCacheKey(), cache);
		}
		*/
		if (!this.distributedMaps.containsKey(rootCacheKey))
		{
			this.distributedMaps.put(rootCacheKey, cache);
		}
	}
	
	public boolean isExisted(String rootCacheKey)
	{
		return this.distributedMaps.containsKey(rootCacheKey);
	}

	public DistributedPersistableChildMap<Key, Value, Factory, DB> get(String rootCacheKey)
	{
		return this.distributedMaps.get(rootCacheKey);
	}
	
	public void unregister(String rootCacheKey)
	{
		this.distributedMaps.remove(rootCacheKey);
	}
}
