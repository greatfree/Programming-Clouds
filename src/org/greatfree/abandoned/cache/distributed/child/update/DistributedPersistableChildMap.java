package org.greatfree.abandoned.cache.distributed.child.update;

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
import org.greatfree.abandoned.cache.distributed.CacheValue;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Tools;

// Created: 07/17/2017, Bing Li
public class DistributedPersistableChildMap implements ChildMapDistributable
{
	private final String cacheKey;
	private final String cacheName;
	private Cache<String, CacheValue> cache;
	private PersistentCacheManager manager;

	private final long cacheSize;

	private ConcurrentSkipListSet<String> keys;
	private StringKeyDB db;
	
	private DistributedChildMapListener listener;
	
	private MapClusterChild child;
	
	private final int subBranchCount;
	
	public DistributedPersistableChildMap(String cacheName, PersistableMapFactorable<String, CacheValue> factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, StringKeyDB db, Peer<DistributedCacheChildDispatcher> peer, int subBranchCount)
	{
		this.cacheKey = Tools.getHash(cacheName);
		this.cacheName = cacheName;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheName, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheKey);
		this.listener = new DistributedChildMapListener(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = cacheSize;
		this.db = db;
		this.keys = db.loadKeys();
		this.child = new MapClusterChild(peer);
		this.subBranchCount = subBranchCount;
	}

	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
	public DistributedPersistableChildMap(DistributedPersistableMapBuilder builder)
	{
		this.cacheKey = Tools.getHash(builder.getCacheName());
		this.cacheName = builder.getCacheName();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheName(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheName);
		this.listener = new DistributedChildMapListener(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		this.db = builder.getDB();
		this.keys = this.db.loadKeys();
		this.child = new MapClusterChild(builder.getPeer());
		this.subBranchCount = builder.getSubBranchCount();
	}

	/*
	 * Define the Builder pattern for the distributed map. 07/10/2017, Bing Li
	 */
	public static class DistributedPersistableMapBuilder implements Builder<DistributedPersistableChildMap>
	{
		private PersistableMapFactorable<String, CacheValue> factory;
		private String rootPath;
		private String cacheName;
		private long cacheSize;
		private long offheapSizeInMB;
		private long diskSizeInMB;
		private StringKeyDB db;
		private Peer<DistributedCacheChildDispatcher> peer;
		private int subBranchCount;

		public DistributedPersistableMapBuilder()
		{
		}

		public DistributedPersistableMapBuilder factory(PersistableMapFactorable<String, CacheValue> factory)
		{
			this.factory = factory;
			return this;
		}
		
		public DistributedPersistableMapBuilder rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public DistributedPersistableMapBuilder cacheName(String cacheName)
		{
			this.cacheName = cacheName;
			return this;
		}

		public DistributedPersistableMapBuilder cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public DistributedPersistableMapBuilder offheapSizeInMB(long offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public DistributedPersistableMapBuilder diskSizeInMB(long diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public DistributedPersistableMapBuilder db(StringKeyDB db)
		{
			this.db = db;
			return this;
		}
		
		public DistributedPersistableMapBuilder peer(Peer<DistributedCacheChildDispatcher> peer)
		{
			this.peer = peer;
			return this;
		}
		
		public DistributedPersistableMapBuilder subBranchCount(int subBranchCount)
		{
			this.subBranchCount = subBranchCount;
			return this;
		}

		@Override
		public DistributedPersistableChildMap build() throws IOException
		{
			return new DistributedPersistableChildMap(this);
		}
		
		public PersistableMapFactorable<String, CacheValue> getFactory()
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

		public StringKeyDB getDB()
		{
			return this.db;
		}
		
		public Peer<DistributedCacheChildDispatcher> getPeer()
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
	public void open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	{
		ChildMapRegistry.CACHE().register(this);
		this.child.init();
	}

	/*
	 * Close the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
	public void close(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		ChildMapRegistry.CACHE().unregister(this);
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
	public void forward(String key, CacheValue value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * Remove the key from the local cache. 07/09/2017, Bing Li
	 */
	@Override
	public void removeDBKey(String key)
	{
		this.keys.remove(key);
	}

	/*
	 * Put the value into the local cache. 07/09/2017, Bing Li
	 */
	@Override
	public void put(String key, CacheValue value)
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
	public void putAll(Map<String, CacheValue> values)
	{
		this.keys.addAll(values.keySet());
		this.cache.putAll(values);
	}

	/*
	 * At this moment, I have NOT decided to how to handle data evicted by the child local cache. So when the below method is invoked, it is supposed that the value should be located at the child local cache. If the value cannot be retrieved by the key, it represents that the value does not exist in the entire cache. I will take into account the issue of how to handle the data that is evicted by the child local cache. 07/14/2017, Bing Li
	 */
	@Override
	public CacheValue get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cache.get(key);
	}

	/*
	 * At this moment, I have NOT decided to how to handle data evicted by the child local cache. So when the below method is invoked, it is supposed that the value should be located at the child local cache. If the value cannot be retrieved by the key, it represents that the value does not exist in the entire cache. I will take into account the issue of how to handle the data that is evicted by the child local cache. 07/14/2017, Bing Li
	 */
	@Override
	public Map<String, CacheValue> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cache.getAll(keys);
	}

	/*
	 * Check whether a key is existed in the local cache. If it does not exist, it indicates that it is not existed in the entire cache in the current version. 07/14/2017, Bing Li
	 */
	@Override
	public boolean isExisted(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cache.containsKey(key);
	}

	/*
	 * Check whether the distributed map is empty or not. 07/14/2017, Bing Li
	 */
	@Override
	public boolean isEmpty()
	{
		return this.keys.size() <= 0;
	}

	/*
	 * Expose the size of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.keys.size();
	}

	/*
	 * Expose the size of the empty size of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cacheSize - this.keys.size();
	}

	/*
	 * Expose the size of the data which is not accessed. The method is identical to the one, getEmptySize(). It is invoked by the sorted map only. I am considering whether it is useful in the distributed environment. 07/14/2017, Bing Li
	 */
	@Override
	public long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.keys.size() - currentAccessedEndIndex - 1;
	}

	/*
	 * Check whether the local cache is full or not. 07/14/2017, Bing Li
	 */
	@Override
	public boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.keys.size() >= this.cacheSize;
	}

	/*
	 * Expose the keys of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.keys;
	}

	/*
	 * Expose the values of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public Map<String, CacheValue> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.cache.getAll(this.keys);
	}

	/*
	 * Remove the values by their keys. 07/14/2017, Bing Li
	 */
	@Override
	public void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.keys.removeAll(keys);
		this.cache.removeAll(keys);
	}

	/*
	 * Remove the value by its key. 07/14/2017, Bing Li
	 */
	@Override
	public void remove(String k) throws InstantiationException, IllegalAccessException, IOException
	{
		this.keys.remove(k);
		this.cache.remove(k);
	}

	/*
	 * Clear the values of the local cache. 07/14/2017, Bing Li
	 */
	@Override
	public void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
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

}
