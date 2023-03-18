package org.greatfree.abandoned.cache.distributed.child;

import java.io.IOException;
import java.util.EnumSet;
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
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Tools;

/*
 * The class is a child of the distributed cache in the cluster.
 */

// Created: 07/08/2017, Bing Li
//public class DistributedPersistableChildMap<Value extends SerializedKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>  implements MapDistributable<Value>
//public class DistributedPersistableChildMap<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>  implements ChildMapDistributable<Value>
//public class DistributedPersistableChildMap<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>  implements ChildMapDistributable<Value, Factory, DB>
public class DistributedPersistableChildMap<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>  implements ChildMapDistributable<Key, Value, Factory, DB>
{
	// The key of the child map. 07/10/2017, Bing Li
	private final String cacheKey;
	// The name of the child map. 07/10/2017, Bing Li
	private final String cacheName;
	// The key of the root map. 07/20/2017, Bing Li
	private final String rootMapKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;

	private final long cacheSize;

	private ConcurrentSkipListSet<String> keys;
	private DB db;

//	private DistributedCacheListener<String, Value, DistributedPersistableChildMap<Value, Factory, DB>> listener;
//	private DistributedMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>> listener;
//	private DistributedChildMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>> listener;
//	private DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Value, Factory, DB>> listener;
//	private DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>> listener;
	private DistributedChildMapListener<Key, Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>> listener;

	private MapClusterChild<Key, Value, Factory, DB> child;
	
	private final int subBranchCount;

	public DistributedPersistableChildMap(String cacheName, String rootMapName, Factory factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, DB db, Peer<DistributedCacheChildDispatcher<Key, Value, Factory, DB>> peer, int subBranchCount)
	{
		this.cacheKey = Tools.getHash(cacheName);
		this.cacheName = cacheName;
		this.rootMapKey = Tools.getHash(rootMapName);
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheName, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheKey);
//		this.listener = new DistributedCacheListener<String, Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>>(this);
		this.listener = new DistributedChildMapListener<Key, Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = cacheSize;
		this.db = db;
		this.keys = db.loadKeys();
//		this.child = new MapClusterChild<Value, Factory, DB>(this.cacheKey, this.rootMapKey, peer);
		this.child = new MapClusterChild<Key, Value, Factory, DB>(peer);
		this.subBranchCount = subBranchCount;
	}

	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
	public DistributedPersistableChildMap(DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> builder)
	{
		this.cacheKey = Tools.getHash(builder.getCacheName());
		this.cacheName = builder.getCacheName();
		this.rootMapKey = Tools.getHash(builder.getRootMapName());
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheName(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheName);
//		this.listener = new DistributedCacheListener<String, Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Value, Factory, DB>>(this);
//		this.listener = new DistributedChildMapListener<Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>>(this);
		this.listener = new DistributedChildMapListener<Key, Value, Factory, DB, DistributedPersistableChildMap<Key, Value, Factory, DB>>(this);
//		this.listener = new DistributedMapListener<Value, DistributedPersistableChildMap<Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		this.db = builder.getDB();
		this.keys = this.db.loadKeys();
//		this.child = new MapClusterChild<Value, Factory, DB>(this.cacheKey, this.rootMapKey, builder.getPeer());
		this.child = new MapClusterChild<Key, Value, Factory, DB>(builder.getPeer());
		this.subBranchCount = builder.getSubBranchCount();
	}
	
	/*
	 * Define the Builder pattern for the distributed map. 07/10/2017, Bing Li
	 */
	public static class DistributedPersistableChildMapBuilder<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements Builder<DistributedPersistableChildMap<Key, Value, Factory, DB>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheName;
		private String rootMapName;
		private long cacheSize;
		private long offheapSizeInMB;
		private long diskSizeInMB;
		private DB db;
		private Peer<DistributedCacheChildDispatcher<Key, Value, Factory, DB>> peer;
		private int subBranchCount;

		public DistributedPersistableChildMapBuilder()
		{
		}

		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> cacheName(String cacheName)
		{
			this.cacheName = cacheName;
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> rootMapName(String rootMapName)
		{
			this.rootMapName = rootMapName;
			return this;
		}

		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> offheapSizeInMB(long offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> diskSizeInMB(long diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> peer(Peer<DistributedCacheChildDispatcher<Key, Value, Factory, DB>> peer)
		{
			this.peer = peer;
			return this;
		}
		
		public DistributedPersistableChildMapBuilder<Key, Value, Factory, DB> subBranchCount(int subBranchCount)
		{
			this.subBranchCount = subBranchCount;
			return this;
		}

		@Override
		public DistributedPersistableChildMap<Key, Value, Factory, DB> build()
		{
			return new DistributedPersistableChildMap<Key, Value, Factory, DB>(this);
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
		
		public String getRootMapName()
		{
			return this.rootMapName;
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
		
		public Peer<DistributedCacheChildDispatcher<Key, Value, Factory, DB>> getPeer()
		{
			return this.peer;
		}
		
		public int getSubBranchCount()
		{
			return this.subBranchCount;
		}
	}

	/*
	 * Open the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
	public void open(ChildMapRegistry<Key, Value, Factory, DB> registry) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		registry.register(this.rootMapKey, this);
		this.child.init();
	}

	/*
	 * Close the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
	public void close(ChildMapRegistry<Key, Value, Factory, DB> registry, long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
//		registry.unregister(this.cacheKey);
		registry.unregister(this.rootMapKey);
		this.db.removeAll();
		this.db.saveKeys(this.keys);
		this.db.dispose();
		this.cache.getRuntimeConfiguration().deregisterCacheEventListener(this.listener);
		this.manager.close();
		this.child.terminate(timeout);
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
	 * At the root, the method is called to forward the specified value to the nearest node in the cluster. The value is the one that is evicted from the root cache. I have NOT decided to how to handle the value that is evicted by the child cache. 07/09/2017, Bing Li
	 */
	@Override
	public void forward(String key, Value value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
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
		this.keys.add(key);
		this.cache.put(key, value);
	}

	/*
	 * Put the values into the local cache. 07/09/2017, Bing Li
	 * 
	 * Usually, values are put into the local cache by unicasting. So the current is not invoked in most time.07/14/2017, Bing Li
	 */
	@Override
	public synchronized void putAll(Map<String, Value> values)
	{
		/*
		for (Map.Entry<String, Value> entry : values.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
		*/
		this.keys.addAll(values.keySet());
		this.cache.putAll(values);
	}

	/*
	 * At this moment, I have NOT decided to how to handle data evicted by the child local cache. So when the below method is invoked, it is supposed that the value should be located at the child local cache. If the value cannot be retrieved by the key, it represents that the value does not exist in the entire cache. I will take into account the issue of how to handle the data that is evicted by the child local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized Value get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cache.get(key);
	}

	/*
	 * At this moment, I have NOT decided to how to handle data evicted by the child local cache. So when the below method is invoked, it is supposed that the value should be located at the child local cache. If the value cannot be retrieved by the key, it represents that the value does not exist in the entire cache. I will take into account the issue of how to handle the data that is evicted by the child local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized Map<String, Value> get(Set<String> keys)
	{
		return this.cache.getAll(keys);
	}

	/*
	 * Check whether a key is existed in the local cache. If it does not exist, it indicates that it is not existed in the entire cache in the current version. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized boolean isExisted(Key key)
	{
		return this.cache.containsKey(key.getDataKey());
	}

	/*
	 * Check whether the distributed map is empty or not. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized boolean isEmpty()
	{
		return this.keys.size() <= 0;
	}

	/*
	 * Expose the size of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized long getSize()
	{
		return this.keys.size();
	}

	/*
	 * Expose the size of the empty size of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized long getEmptySize()
	{
		return this.cacheSize - this.keys.size();
	}

	/*
	 * Expose the size of the data which is not accessed. The method is identical to the one, getEmptySize(). It is invoked by the sorted map only. I am considering whether it is useful in the distributed environment. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized long getLeftSize(long currentAccessedEndIndex)
	{
		return this.keys.size() - currentAccessedEndIndex - 1;
	}

	/*
	 * Check whether the local cache is full or not. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized boolean isCacheFull()
	{
		return this.keys.size() >= this.cacheSize;
	}

	/*
	 * Expose the keys of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized Set<String> getKeys()
	{
		return this.keys;
	}

	/*
	 * Expose the values of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized Map<String, Value> getValues()
	{
		return this.cache.getAll(this.keys);
	}

	/*
	 * Remove the values by their keys. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized void remove(HashSet<String> keys)
	{
		this.keys.removeAll(keys);
		this.cache.removeAll(keys);
	}

	/*
	 * Remove the value by its key. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized void remove(Key k)
	{
		this.keys.remove(k.getDataKey());
		this.cache.remove(k.getDataKey());
	}

	/*
	 * Clear the values of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public synchronized void clear()
	{
		this.keys.clear();
		this.cache.clear();
	}

	/*
	 * Broadcast the keys within the cluster to retrieve values by given keys. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastGivenKeysRequest(BroadGetRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastGivenKeysRequest(request, this.subBranchCount);
	}

	/*
	 * Broadcast the request to get the size of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastGetSizeRequest(BroadSizeRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastGetSizeRequest(request, this.subBranchCount);
	}

	/*
	 * Broadcast the request to get all of the keys of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastGetAllKeysRequest(BroadKeysRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastGetAllKeysRequest(request, this.subBranchCount);
	}

	/*
	 * Broadcast the request to get all of the values of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastGetAllValuesRequest(BroadValuesRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastGetAllValuesRequest(request, this.subBranchCount);
	}

	/*
	 * Broadcast the notification to remove keys to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastRemovalKeysNotification(RemoveKeysNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastRemovalKeysNotification(notification, this.subBranchCount);
	}

	/*
	 * Broadcast the notification to clear the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastClearNotification(ClearNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastClearNotification(notification, this.subBranchCount);
	}

	/*
	 * Broadcast the notification to close the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	@Override
	public void broadcastCloseNotification(CloseNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.child.broadcastCloseNotification(notification, this.subBranchCount);
	}

	/*
	 * The result to a query is returned to the root through the method. 07/23/2017, Bing Li
	 */
	@Override
	public void respondToRoot(ServerMessage message) throws IOException, InterruptedException
	{
		this.child.respondToRoot(message);
	}
}
