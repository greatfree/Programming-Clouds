package org.greatfree.abandoned.cache.distributed.root.update;

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
import org.greatfree.abandoned.cache.distributed.root.DistributedCacheRootDispatcher;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.Tools;

// Created: 07/16/2017, Bing Li
public class DistributedPersistableMap implements MapDistributable
{
	// The key is used to represent the cache, especially in the case when one response is received, in which the response is dispatched to its original distributed map by the key. 07/13/2017, Bing Li
	private final String cacheKey;
	// The key of the map. 07/10/2017, Bing Li
	private final String cacheName;
	// The cache from Ehcache. 07/10/2017, Bing Li
	private Cache<String, CacheValue> cache;
	// The cache manager from Ehcache. 07/10/2017, Bing Li
	private PersistentCacheManager manager;

	// The size of the map. 07/10/2017, Bing Li
	private final long cacheSize;
	
	// The total cache size should be the cache size multiplying the number of nodes in the cluster. 07/11/2017, Bing Li
	private final long totalCacheSize;

	// The keys in the map. I creates it by employing Berkeley DB. 07/10/2017, Bing Li
	private ConcurrentSkipListSet<String> keys;
	// The Berkeley DB. 07/10/2017, Bing Li
	private StringKeyDB db;
	
	// The listener defined by Ehcache. Evicted data is broadcast by it to the cluster. 07/10/2017, Bing Li
	private DistributedMapListener listener;
	
	// The map cluster root, which accomplishes the goals to broadcast data of the distributed cache. 07/10/2017, Bing Li
	private MapClusterRoot root;

	// The root branch of the cluster. 07/10/2017, Bing Li
	private final int rootBranchCount;
	// The children branch of the cluster. 07/10/2017, Bing Li
	private final int subBranchCount;

	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
//	public DistributedPersistableMap(String cacheName, PersistableMapFactorable<String, CacheValue> factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, StringKeyDB db, Peer<DistributedCacheRootDispatcher> peer, int rootBranchCount, int subBranchCount)
	public DistributedPersistableMap(String cacheName, PersistableMapFactorable<String, CacheValue> factory, String rootPath, long cacheSize, long offheapSizeInMB, long diskSizeInMB, StringKeyDB db, Peer<DistributedCacheRootDispatcher> peer, int rootBranchCount, int subBranchCount)
	{
		this.cacheKey = Tools.getHash(cacheName);
		this.cacheName = cacheName;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheName, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheName);
		this.listener = new DistributedMapListener(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = cacheSize;
		this.totalCacheSize = this.cacheSize * peer.getClientPool().getClientKeys().size();
		this.db = db;
		this.keys = db.loadKeys();
		this.root = new MapClusterRoot(peer);
		this.rootBranchCount = rootBranchCount;
		this.subBranchCount = subBranchCount;
	}

	/*
	 * Initialize the distributed map. 07/10/2017, Bing Li
	 */
	public DistributedPersistableMap(DistributedPersistableMapBuilder builder)
	{
		this.cacheKey = Tools.getHash(builder.getCacheName());
		this.cacheName = builder.getCacheName();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheName(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheName);
		this.listener = new DistributedMapListener(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		this.totalCacheSize = builder.getCacheSize() * builder.getPeer().getClientPool().getClientKeys().size();
		this.db = builder.getDB();
		this.keys = this.db.loadKeys();
		this.root = new MapClusterRoot(builder.getPeer());
		this.rootBranchCount = builder.getRootBranchCount();
		this.subBranchCount = builder.getSubBranchCount();
	}

	/*
	 * Define the Builder pattern for the distributed map. 07/10/2017, Bing Li
	 */
	public static class DistributedPersistableMapBuilder implements Builder<DistributedPersistableMap>
	{
		private PersistableMapFactorable<String, CacheValue> factory;
		private String rootPath;
		private String cacheName;
		private long cacheSize;
		private long offheapSizeInMB;
		private long diskSizeInMB;
		private StringKeyDB db;
		private Peer<DistributedCacheRootDispatcher> peer;
		private int rootBranchCount;
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
		
		public DistributedPersistableMapBuilder peer(Peer<DistributedCacheRootDispatcher> peer)
		{
			this.peer = peer;
			return this;
		}
		
		public DistributedPersistableMapBuilder rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}
		
		public DistributedPersistableMapBuilder subBranchCount(int subBranchCount)
		{
			this.subBranchCount = subBranchCount;
			return this;
		}

		@Override
		public DistributedPersistableMap build()
		{
			return new DistributedPersistableMap(this);
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
	}

	/*
	 * Open the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
	public void open() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	{
		MapRegistry.CACHE().register(this);
		this.root.init();
	}

	/*
	 * Close the distributed persistable map. 07/09/2017, Bing Li
	 */
	@Override
	public void close(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		MapRegistry.CACHE().unregister(this);
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
	 * Forward the specified value to the nearest node in the cluster. The method is invoked when the value is evicted from the cache. 07/09/2017, Bing Li
	 */
	@Override
	public void forward(String key, CacheValue value) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
//		value.setCacheKey(this.cacheKey);
		this.root.unicastNotifyNearestly(value, this.rootBranchCount, this.subBranchCount);
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
	 */
	@Override
	public void putAll(Map<String, CacheValue> values)
	{
		this.keys.addAll(values.keySet());
		this.cache.putAll(values);
	}

	/*
	 * Retrieve the value according to its key from the local cache first. If it is not available, retrieve it from the nearest node in the cluster. 07/09/2017, Bing Li
	 */
	@Override
	public CacheValue get(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, CacheValue> get(Set<String> keys) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExisted(String key) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getSize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getEmptySize() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.totalCacheSize - this.getSize();
	}

	@Override
	public long getLeftSize(long currentAccessedEndIndex) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isCacheFull() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getKeys() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, CacheValue> getValues() throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(HashSet<String> keys) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String k) throws InstantiationException, IllegalAccessException, IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCacheName()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
