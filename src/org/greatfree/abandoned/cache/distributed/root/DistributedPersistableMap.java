package org.greatfree.abandoned.cache.distributed.root;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.BroadGetResponse;
import org.greatfree.cache.message.BroadKeysResponse;
import org.greatfree.cache.message.BroadSizeResponse;
import org.greatfree.cache.message.BroadValuesResponse;
import org.greatfree.cache.message.UniGetResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.testing.cache.distributed.IntegerValue;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

/*
 * The map, com.greatfree.cache.PersistableMap, is suitable for the parent of the distributed map. The key, value, and listener should be updated. It affects too many other ones. I need to create another one for the distributed map. This is the new distributed map I created. 07/08/2017, Bing Li
 */

// Created: 07/08/2017, Bing Li
//public class DistributedPersistableMap<Value extends SerializedKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements MapDistributable<String, Value>
//public class DistributedPersistableMap<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements MapDistributable<Value>
//public class DistributedPersistableMap<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements MapDistributable<Value, Factory, DB>
public class DistributedPersistableMap<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements MapDistributable<Key, Value, Factory, DB>
{
	// The key is used to represent the cache, especially in the case when one response is received, in which the response is dispatched to its original distributed map by the key. 07/13/2017, Bing Li
	private final String cacheKey;
	// The key of the map. 07/10/2017, Bing Li
	private final String cacheName;
	// The cache from Ehcache. 07/10/2017, Bing Li
	private Cache<String, Value> cache;
	// The cache manager from Ehcache. 07/10/2017, Bing Li
	private PersistentCacheManager manager;

	// The size of the map. 07/10/2017, Bing Li
	private final long cacheSize;
	
	// The total cache size should be the cache size multiplying the number of nodes in the cluster. 07/11/2017, Bing Li
	private final long totalCacheSize;

	// The keys in the map. I creates it by employing Berkeley DB. 07/10/2017, Bing Li
	private ConcurrentSkipListSet<String> keys;
	// The Berkeley DB. 07/10/2017, Bing Li
	private DB db;
	
	// The listener defined by Ehcache. Evicted data is broadcast by it to the cluster. 07/10/2017, Bing Li
//	private DistributedCacheListener<String, Value, DistributedPersistableMap<Value, Factory, DB>> listener;
//	private DistributedMapListener<Value, DistributedPersistableMap<Value, Factory, DB>> listener;
//	private DistributedMapListener<Value, Factory, DB, DistributedPersistableMap<Value, Factory, DB>> listener;
//	private DistributedMapListener<Value, Factory, DB, DistributedPersistableMap<Key, Value, Factory, DB>> listener;
	private DistributedMapListener<Key, Value, Factory, DB, DistributedPersistableMap<Key, Value, Factory, DB>> listener;
	
	// The map cluster root, which accomplishes the goals to broadcast data of the distributed cache. 07/10/2017, Bing Li
//	private MapClusterRoot<Value> root;
	private MapClusterRoot<Key, Value> root;
	
	// The root branch of the cluster. 07/10/2017, Bing Li
	private final int rootBranchCount;
	// The children branch of the cluster. 07/10/2017, Bing Li
	private final int subBranchCount;
	
	// The registry that keeps all of the instances of distributed maps. 07/13/2017, Bing Li
//	private MapRegistry<Value, Factory, DB> registry;
	
	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
//	public DistributedPersistableMap(String cacheName, Factory factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, DB db, Peer<DistributedCacheRootDispatcher> peer, int rootBranchCount, int subBranchCount, MapRegistry<Value, Factory, DB> registry)
	public DistributedPersistableMap(String cacheName, Factory factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, DB db, Peer<DistributedCacheRootDispatcher> peer, int rootBranchCount, int subBranchCount)
	{
		this.cacheKey = Tools.getHash(cacheName);
		this.cacheName = cacheName;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheName, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheName);
//		this.listener = new DistributedCacheListener<String, Value, DistributedPersistableMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, DistributedPersistableMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, Factory, DB, DistributedPersistableMap<Value, Factory, DB>>(this);
		this.listener = new DistributedMapListener<Key, Value, Factory, DB, DistributedPersistableMap<Key, Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = cacheSize;
		this.totalCacheSize = this.cacheSize * peer.getClientPool().getClientKeys().size();
		this.db = db;
		this.keys = db.loadKeys();
//		this.root = new MapClusterRoot<Value>(peer);
		this.root = new MapClusterRoot<Key, Value>(peer);
//		this.root = new MapClusterRoot<Value>(this.cacheKey, peer);
		this.rootBranchCount = rootBranchCount;
		this.subBranchCount = subBranchCount;
//		this.registry = registry;
//		this.registry.put(this.cacheKey, this);
	}

	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
	public DistributedPersistableMap(DistributedPersistableMapBuilder<Key, Value, Factory, DB> builder)
	{
		this.cacheKey = Tools.getHash(builder.getCacheName());
		this.cacheName = builder.getCacheName();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheName(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheName);
//		this.listener = new DistributedCacheListener<String, Value, DistributedPersistableMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, DistributedPersistableMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, Factory, DB, DistributedPersistableMap<Value, Factory, DB>>(this);
		this.listener = new DistributedMapListener<Key, Value, Factory, DB, DistributedPersistableMap<Key, Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		this.totalCacheSize = builder.getCacheSize() * builder.getPeer().getClientPool().getClientKeys().size();
		this.db = builder.getDB();
		this.keys = this.db.loadKeys();
//		this.root = new MapClusterRoot<Value>(builder.getPeer());
		this.root = new MapClusterRoot<Key, Value>(builder.getPeer());
//		this.root = new MapClusterRoot<Value>(this.cacheKey, builder.getPeer());
		this.rootBranchCount = builder.getRootBranchCount();
		this.subBranchCount = builder.getSubBranchCount();
//		this.registry = builder.getRegistry();
//		this.registry.put(this.cacheKey, this);
	}
	
	/*
	 * Define the Builder pattern for the distributed map. 07/10/2017, Bing Li
	 */
//	public static class DistributedPersistableMapBuilder<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements Builder<DistributedPersistableMap<Value, Factory, DB>>
	public static class DistributedPersistableMapBuilder<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements Builder<DistributedPersistableMap<Key, Value, Factory, DB>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheName;
		private long cacheSize;
		private long offheapSizeInMB;
		private long diskSizeInMB;
		private DB db;
		private Peer<DistributedCacheRootDispatcher> peer;
		private int rootBranchCount;
		private int subBranchCount;
//		private MapRegistry<Value, Factory, DB> registry;

		public DistributedPersistableMapBuilder()
		{
		}

		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> cacheName(String cacheName)
		{
			this.cacheName = cacheName;
			return this;
		}

		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> offheapSizeInMB(long offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> diskSizeInMB(long diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> peer(Peer<DistributedCacheRootDispatcher> peer)
		{
			this.peer = peer;
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}
		
		public DistributedPersistableMapBuilder<Key, Value, Factory, DB> subBranchCount(int subBranchCount)
		{
			this.subBranchCount = subBranchCount;
			return this;
		}

		/*
		public PersistableMapBuilder<Value, Factory, DB> registry(MapRegistry<Value, Factory, DB> registry)
		{
			this.registry = registry;
			return this;
		}
		*/

		@Override
		public DistributedPersistableMap<Key, Value, Factory, DB> build()
		{
			return new DistributedPersistableMap<Key, Value, Factory, DB>(this);
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public String getCacheName()
		{
			return this.cacheName;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
		}
		
		public long getOffheapSizeInMB()
		{
			return this.offheapSizeInMB;
		}
		
		public long getDiskSizeInMB()
		{
			return this.diskSizeInMB;
		}

		public DB getDB()
		{
			return this.db;
		}
		
		public Peer<DistributedCacheRootDispatcher> getPeer()
		{
			return this.peer;
		}
		
		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}
		
		public int getSubBranchCount()
		{
			return this.subBranchCount;
		}

		/*
		public MapRegistry<Value, Factory, DB> getRegistry()
		{
			return this.registry;
		}
		*/
	}

	/*
	 * Open the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
//	public void open(MapRegistry<Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	public void open(MapRegistry<Key, Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	{
		registry.register(this);
		this.root.init();
	}

	/*
	 * Close the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
//	public void close(MapRegistry<Value, Factory, DB> registry) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	public void close(MapRegistry<Key, Value, Factory, DB> registry, long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		registry.unregister(this.cacheKey);
		this.db.removeAll();
		this.db.saveKeys(this.keys);
		this.db.dispose();
		this.cache.getRuntimeConfiguration().deregisterCacheEventListener(this.listener);
		this.manager.close();
		this.root.terminate(timeout);
	}

	/*
	 * Expose the cache key. 07/09/2017, Bing Li
	 */
	@Override
	public String getCacheKey()
	{
		return this.cacheKey;
	}

	/*
	 * Expose the cache name. 07/13/2017, Bing Li
	 */
	@Override
	public String getCacheName()
	{
		return this.cacheName;
	}

	/*
	 * Forward the specified value to the nearest node in the cluster. The method is invoked when the value is evicted from the cache. 07/09/2017, Bing Li
	 */
	@Override
	public void forward(String key, Value value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// For testing ONLY. 07/20/2017, Bing Li
		// Start testing. 07/20/2017, Bing Li
		IntegerValue v = (IntegerValue)value;
		System.out.println("(Key = " + value.getDataKey() + ", Value = " + v.getValue() + ") is being distributed to the distributed map ...");
		// Testing is done. 07/20/2017, Bing Li

//		System.out.println("Value: " + value.getDataKey() + " is forwarded to the cluster ...");
		value.setCacheKey(this.cacheKey);
		this.root.unicastNotifyNearestly(value, this.rootBranchCount, this.subBranchCount);
	}

	/*
	 * Remove the key from the local cache. 07/09/2017, Bing Li
	 */
	@Override
	public synchronized void removeDBKey(String key)
	{
		this.keys.remove(key);
	}

	/*
	 * When retrieved value is obtained from the cluster, the method is called for collection. 07/09/2017, Bing Li
	 */
	/*
	@Override
	public void valueReceived(String key, Value value)
	{
	}
	*/

	/*
	 * Put the value into the local cache. 07/09/2017, Bing Li
	 */
	@Override
	public synchronized void put(String key, Value value)
	{
//		value.setCacheKey(this.cacheKey);
		this.keys.add(key);
		this.cache.put(key, value);
	}

	/*
	 * Put the values into the local cache. 07/09/2017, Bing Li
	 */
	@Override
	public synchronized void putAll(Map<String, Value> values)
	{
		this.keys.addAll(values.keySet());
		this.cache.putAll(values);
	}

	/*
	 * Retrieve the value according to its key from the local cache first. If it is not available, retrieve it from the nearest node in the cluster. 07/09/2017, Bing Li
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
//	public synchronized Value get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public synchronized Value get(Key key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get the data from the local cache first. 07/10/2017, Bing Li
//		Value value = this.cache.get(key);
		Value value = this.cache.get(key.getDataKey());
		if (value == null)
		{
			key.setCacheKey(this.cacheKey);
			// If the data is not available in the local cache, it needs to be retrieved from the cluster. 07/10/2017, Bing Li
//			Map<String, UniGetResponse<String, Value>> responses = this.root.unicastReadNearestly(key, this.rootBranchCount, this.subBranchCount);
			Map<String, UniGetResponse<Value>> responses = this.root.unicastReadNearestly(key, this.rootBranchCount, this.subBranchCount);
			if (responses != null)
			{
				// Since the data is retrieved by users, it needs to put it into the local cache for possible near future accessing. 07/10/2017, Bing Li
//				this.put(key, responses.get(key).getValue());
				this.put(key.getDataKey(), responses.get(key).getValue());
				return responses.get(key).getValue();
			}
		}
		return value;
	}

	/*
	 * Retrieve the values according to the keys from the local cache first. If some of them are not available, retrieve them form the nearest nodes in the cluster. 07/10/2017, Bing Li
	 */
	@Override
	public synchronized Map<String, Value> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get data from the local cache first. 07/10/2017, Bing Li
		Map<String, Value> values = this.cache.getAll(keys);
		// Get the keys of the retrieved data from the local cache. 07/10/2017, Bing Li
		Set<String> retrievedKeys = values.keySet();
		// Get the keys that are not retrieved from the local cache. 07/10/2017, Bing Li
		HashSet<String> notRetrievedKeys = new HashSet<String>(Sets.difference(keys, retrievedKeys));
		// Get the data that are not retrieved from the local cache through the cluster. 07/10/2017, Bing Li
		Map<String, BroadGetResponse<Value>> clusteredValues = this.root.broadcastReadNearestly(notRetrievedKeys, this.rootBranchCount, this.subBranchCount);
		// Put all of retrieved data together. 07/10/2017, Bing Li
		for (BroadGetResponse<Value> response : clusteredValues.values())
		{
			values.putAll(response.getValues());
		}
		return values;
	}

	/*
	 * Check whether the specified key exists in the distributed map or not. 07/10/2017, Bing Li
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
//	public synchronized boolean isExisted(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public synchronized boolean isExisted(Key key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Check whether the specified data is in the local cache or not. 07/10/2017, Bing Li
		Value v = this.cache.get(key.getDataKey());
		if (v != null)
		{
			return true;
		}
		else
		{
			// If the data is not existed in the local cache, it needs to retrieve it from the cluster. 07/10/2017, Bing Li
			Map<String, UniGetResponse<Value>> responses = this.root.unicastReadNearestly(key, this.rootBranchCount, this.subBranchCount);
			if (responses != null)
			{
				// Since the data is retrieved by users, it needs to put it into the local cache for possible near future accessing. 07/10/2017, Bing Li
				this.put(key.getDataKey(), responses.get(key).getValue());
				return true;
			}
		}
		return false;
	}

	/*
	 * Check whether the distributed map is empty or not. The method does not need to interact with the cluster since the local cache is the core. If it is empty, the cluster should be empty as well. 07/10/2017, Bing Li
	 */
	@Override
	public synchronized boolean isEmpty()
	{
		return this.keys.size() <= 0;
	}

	/*
	 * Another case resulting in remotely accessing is that the cache size is greater than that of the keys. However, it was ever less than that of the keys. After some data is removed, the cache size is greater than that of the keys again. In this case, the size of the map should always take into account the nodes in the cluster. 07/12/2017, Bing Li
	 * 
	 * Check the size of the map. When the current size of the local cache exceeds the maximum cache size, a broadcast request is required for the method since data of the map is probably existed any corners of the cluster. 07/10/2017, Bing Li 
	 */
	@Override
	public synchronized long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// If the local cache is not full, the size of the local size is equal to that of the distributed map. 07/11/2017, Bing Li
//		if (this.keys.size() <= this.cacheSize)
//		if (this.cacheSize > this.keys.size())
//		{
//			return this.keys.size();
//		}
//		else
//		{
		// If the local cache is full, the size of the distributed map must be retrieved from the entire cluster. The sum of those caches is the size of the distributed cache. 07/11/2017, Bing Li
		Map<String, BroadSizeResponse> responses = this.root.broadcastReadSize(this.rootBranchCount, this.subBranchCount);
		// Get the size of the local cache. 07/11/2017, Bing Li
		long size = this.keys.size();
		// Sum the sizes of each cache of the nodes in the cluster. 07/11/2017, Bing Li
		for (BroadSizeResponse response : responses.values())
		{
			size += response.getSize();
		}
		return size;
//		}
	}

	/*
	 * Get the size which contains nothing in the distributed map. 07/11/2017, Bing Li
	 */
	@Override
	public synchronized long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
//		return this.cacheSize - this.keys.size();
		return this.totalCacheSize - this.getSize();
	}

	/*
	 * Expose the size of the data which is not accessed. It is invoked by the sorted map only. I am considering whether it is useful in the distributed environment. 07/11/2017, Bing Li
	 */
	@Override
	public synchronized long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.getSize() - currentAccessedEndIndex - 1;
	}

	/*
	 * Check whether the distributed cache is full. 07/11/2017, Bing Li
	 */
	@Override
	public synchronized boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
//		return this.cacheSize <= this.keys.size();
		return this.totalCacheSize <= this.getSize();
	}

	/*
	 * 
	 * 	Another case resulting in remotely accessing is that the cache size is greater than that of the keys. However, it was ever less than that of the keys. After some data is removed, the cache size is greater than that of the keys again. In this case, the size of the map should always take into account the nodes in the cluster. 07/12/2017, Bing Li
	 * 
	 * Obtain the keys of the distributed map. Different from traditional maps, the distributed map must cause multicasting operations. When the local cache is full, it is required to do that.
	 */
	@Override
	public synchronized Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Check whether the local cache is full or not. 07/11/2017, Bing Li
//		if (this.cacheSize > this.keys.size())
//		{
			// If not, the keys for the local cache is the keys of the entire distributed map. 07/11/2017, Bing Li
//			return this.keys;
//		}
//		else
//		{
		// If the local cache is full, the keys of the entire map should be retrieved from all of the nodes in the cluster. 0711/2011
		Map<String, BroadKeysResponse> responses = this.root.broadcastReadKeys(this.rootBranchCount, this.subBranchCount);
		Set<String> allKeys = Sets.newHashSet(this.keys);
		// Merge the keys of each node of the cluster. 07/11/2017, Bing Li
		for (BroadKeysResponse response : responses.values())
		{
			allKeys.addAll(response.getKeys());
		}
		return allKeys;
//		}
	}

	/*
	 * 
	 * Another case resulting in remotely accessing is that the cache size is greater than that of the keys. However, it was ever less than that of the keys. After some data is removed, the cache size is greater than that of the keys again. In this case, the size of the map should always take into account the nodes in the cluster. 07/12/2017, Bing Li
	 * 
	 * Obtain all of the values of the distributed map. Multicasting reading is required. 07/11/2017, Bing Li
	 */
	@Override
	public synchronized Map<String, Value> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Check whether the local cache is full or not. 07/12/2017, Bing Li
//		if (this.cacheSize > this.keys.size())
//		{
//			return this.cache.getAll(this.keys);
//		}
//		else
//		{
		// Get the local values first. 07/12/2017, Bing Li
		Map<String, Value> localValues = this.cache.getAll(this.keys);
		// Get other values from the cluster. 07/12/2017, Bing Li
		Map<String, BroadValuesResponse<Value>> responses = this.root.broadcastReadValues(this.rootBranchCount, this.subBranchCount);
		Map<String, Value> values = new HashMap<String, Value>();
		// Merge them together. 07/12/2017, Bing Li
		for (BroadValuesResponse<Value> response : responses.values())
		{
			values.putAll(response.getValues());
		}
		values.putAll(localValues);
		return values;
//		}
	}

	/*
	 * Remove the keys and associated values from the distributed map. Since some keys probably do not exist in the local cache, a broadcast is required for that operation. 07/12/2017, Bing Li
	 */
	@Override
	public synchronized void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Remove the keys from the local cache keys. 07/12/2017, Bing Li
		this.keys.removeAll(keys);
		//  Remove the keys from the local cache. 07/12/2017, Bing Li
		this.cache.removeAll(keys);
		// Remove the keys from the cluster. 07/12/2017, Bing Li
		this.root.broadcastRemovalKeys(keys, this.rootBranchCount, this.subBranchCount);
	}

	/*
	 * Remove the key from the distributed map. If the specified key does not exist in the local cache, it is required to send a unicast notification to the nearest node in the cluster. 07/12/2017, Bing Li
	 */
	@Override
	public synchronized void remove(String key) throws InstantiationException, IllegalAccessException, IOException
	{
		// Check whether the specified key exists in the cache. 07/12/2017, Bing Li
		if (this.cache.containsKey(key))
		{
			// If it exists, remove the key from the local cache. 07/12/2017, Bing Li
			this.keys.remove(key);
			this.cache.remove(key);
		}
		else
		{
			// Unicast the notification to the nearest node in the cluster. 07/12/2017, Bing Li
			this.root.unicastNotifyNearestly(key, this.rootBranchCount, this.subBranchCount);
		}
	}

	/*
	 * Clear the distributed map. It is required to broadcast the notification to the cluster. 07/12/2017, Bing Li
	 */
	@Override
	public synchronized void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Clear the local cache. 07/12/2017, Bing Li
		this.keys.clear();
		this.cache.clear();
		
		// Broadcast the clearance notification to the cluster. 07/12/2017, Bing Li
		this.root.broadcastClear(this.rootBranchCount, this.subBranchCount);
	}

}
