package org.greatfree.cache.distributed.terminal;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.StoreElement;
import org.greatfree.cache.db.MapKeysDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The cache is tested in the Clouds project. 08/27/2018, Bing Li
 */

/*
 * The version is not tested. Only its locking is updated. 08/22/2018, Bing Li
 * 
 * Its counterpart is MapStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/31/2018, Bing Li
// public class TerminalMapStore<Value extends CacheElement<String>, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
public class TerminalMapStore<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	
	private MapKeysDB keysDB;
	private Map<String, Set<String>> keys;
	
	private CacheListener<String, Value, TerminalMapStore<Value, Factory, CompoundKeyCreator, DB>> listener;
	
	private CompoundKeyCreator keyCreator;
//	private ReentrantReadWriteLock lock;

	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	public TerminalMapStore(TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());
		this.listener = new CacheListener<String, Value, TerminalMapStore<Value, Factory, CompoundKeyCreator, DB>>(this);

		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		this.keysDB = new MapKeysDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.MAP_KEYS));
		this.keys = this.keysDB.getAllKeys();

//		this.lock = new ReentrantReadWriteLock();

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}
	
//	public static class TerminalMapStoreBuilder<Value extends CacheElement<String>, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> implements Builder<TerminalMapStore<Value, Factory, CompoundKeyCreator, DB>>
	public static class TerminalMapStoreBuilder<Value extends StoreElement, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> implements Builder<TerminalMapStore<Value, Factory, CompoundKeyCreator, DB>>
	{
		private String storeKey;
		private Factory factory;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private DB db;
		private int alertEvictedCount;

		public TerminalMapStoreBuilder()
		{
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalMapStoreBuilder<Value, Factory, CompoundKeyCreator, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalMapStore<Value, Factory, CompoundKeyCreator, DB> build()
		{
			return new TerminalMapStore<Value, Factory, CompoundKeyCreator, DB>(this);
		}

		public String getStoreKey()
		{
			return this.storeKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
		}
		
		public CompoundKeyCreator getKeyCreator()
		{
			return this.keyCreator;
		}

		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getTotalStoreSize()
		{
			return this.totalStoreSize;
		}
		
		public int getOffheapSizeInMB()
		{
			return this.offheapSizeInMB;
		}
		
		public int getDiskSizeInMB()
		{
			return this.diskSizeInMB;
		}
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
		}
	}
	
	public void close()
	{
		this.manager.close();
//		this.lock.writeLock().lock();
		
		this.keysDB.removeAll();
		this.keysDB.putAll(this.keys);
		this.keysDB.close();
		
		this.db.close();
//		this.lock.writeLock().unlock();
//		this.lock = null;
	}

	public Set<String> getCacheKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.keySet();
	}
	
	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.size() <= 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.size() <= 0;
	}

	public void put(String mapKey, String k, Value v)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, k);

//		this.lock.readLock().lock();
		if (!this.keys.containsKey(mapKey))
		{
//			Set<String> keys = Sets.newHashSet();
			Set<String> keys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(mapKey, keys);
			Set<String> kSet = this.keys.get(mapKey);
			kSet.add(key);
			this.keys.put(mapKey, kSet);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			Set<String> kSet = this.keys.get(mapKey);
			kSet.add(key);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(mapKey, kSet);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.put(key, v);
	}

	public void putAll(String mapKey, Map<String, Value> values)
	{
		String key;
		for (Map.Entry<String, Value> entry : values.entrySet())
		{
			key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
			this.cache.put(key, entry.getValue());
		}
//		this.lock.readLock().lock();
		if (!this.keys.containsKey(mapKey))
		{
//			Set<String> keys = Sets.newHashSet();
			Set<String> keys = new HashSet<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.put(mapKey, keys);
			for (Map.Entry<String, Value> entry : values.entrySet())
			{
				key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
//				this.cache.put(key, entry.getValue());
				Set<String> kSet = this.keys.get(mapKey);
				kSet.add(key);
				this.keys.put(mapKey, kSet);
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			for (Map.Entry<String, Value> entry : values.entrySet())
			{
				key = this.keyCreator.createCompoundKey(mapKey, entry.getKey());
//				this.cache.put(key, entry.getValue());
				Set<String> kSet = this.keys.get(mapKey);
				kSet.add(key);
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.put(mapKey, kSet);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
	}

	public Value get(String mapKey, String k)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, k);
		Value v = this.cache.get(key);
		if (v != null)
		{
			return v;
		}
		/*
		this.lock.readLock().lock();
		try
		{
			return this.db.get(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.db.get(key);
	}
	
	public Map<String, Value> get(String mapKey, Set<String> keys)
	{
//		Set<String> cKeys = Sets.newHashSet();
		Value v;
		Map<String, Value> values = new HashMap<String, Value>();
		for (String key : keys)
		{
			v = this.get(mapKey, key);
			if (v != null)
			{
				values.put(key, v);
			}
		}
		// The below lines always return the values whose size is equal to the number of keys even though one of the values is null. 08/25/2018, Bing Li
//		Map<String, Value> cValues = this.cache.getAll(cKeys);
		/*
		if (cValues != null)
		{
			if (cValues.size() < keys.size())
			{
				this.lock.readLock().lock();
				Map<String, Value> dValues = this.db.getMap(cKeys);
				this.lock.readLock().unlock();
				if (dValues != null)
				{
					if (dValues.size() > 0)
					{
						cValues.putAll(dValues);
						return cValues;
					}
				}
			}
		}
		return null;
		*/
		return values;
	}
	
	public boolean isExisted(String mapKey, String key)
	{
		String rscKey = this.keyCreator.createCompoundKey(mapKey, key);
		Value v = this.cache.get(rscKey);
		if (v != null)
		{
			return true;
		}
//		this.lock.readLock().lock();
		v = this.db.get(rscKey);
//		this.lock.readLock().unlock();
		if (v != null)
		{
			return true;
		}
		return false;
	}

	public boolean isExisted(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.containsKey(mapKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.containsKey(mapKey);
	}

	public long getSize(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(mapKey))
			{
				return this.keys.get(mapKey).size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.containsKey(mapKey))
		{
			return this.keys.get(mapKey).size();
		}
		return 0;
	}

	public long getEmptySize(String mapKey)
	{
		return this.cacheSize - this.getSize(mapKey);
	}
	
	public long getLeftSize(String mapKey, int currentAccessedEndIndex)
	{
		return this.getSize(mapKey) - currentAccessedEndIndex - 1;
	}
	
	public boolean isCacheFull(String mapKey)
	{
		return this.cacheSize <= this.getSize(mapKey);
	}

	public Set<String> getKeys(String mapKey)
	{
		Set<String> rscKeys = null;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(mapKey))
		{
			rscKeys = this.keys.get(mapKey);
		}
//		this.lock.readLock().unlock();
		if (rscKeys != null)
		{
//			Map<String, Value> cValues = this.cache.getAll(rscKeys);
//			Set<String> keys = Sets.newHashSet();
			Set<String> keys = new HashSet<String>();
			for (String entry : rscKeys)
			{
				if (this.cache.containsKey(entry))
				{
					keys.add(entry);
				}
				
			}
			if (keys.size() >= rscKeys.size())
			{
//				return cValues.keySet();
				return keys;
			}
//			this.lock.readLock().lock();
			Map<String, Value> dValues = this.db.getMap(rscKeys);
//			this.lock.readLock().unlock();
			if (dValues != null)
			{
				keys.addAll(dValues.keySet());
				return keys;
			}
		}
		return null;
	}
	
	public Map<String, Value> getValues(String mapKey, int size)
	{
		Set<String> rscsKeys = this.keys.get(mapKey);
		Map<String, Value> values = new HashMap<String, Value>();
		Value v;
		for (String entry : rscsKeys)
		{
			v = this.cache.get(entry);
			if (v != null)
			{
				values.put(entry, v);
				if (values.size() >= size)
				{
					return values;
				}
			}
		}
		return values;
	}

	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 */
	/*
	public Map<String, Value> getValues(String mapKey)
	{
		Set<String> rscKeys;
		this.lock.readLock().lock();
		try
		{
			if (this.keys.containsKey(mapKey))
			{
				rscKeys = this.keys.get(mapKey);
				if (rscKeys != null)
				{
					Map<String, Value> cValues = this.cache.getAll(rscKeys);
					if (cValues.size() >= rscKeys.size())
					{
						return cValues;
					}
					Map<String, Value> dValues = this.db.getMap(rscKeys);
					if (dValues != null)
					{
						cValues.putAll(dValues);
						return cValues;
					}
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}
	*/

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String mapKey, Set<String> keys)
	{
		String key;
//		Set<String> removalKeys = Sets.newHashSet();
		Set<String> removalKeys = new HashSet<String>();
		for (String k : keys)
		{
			key = this.keyCreator.createCompoundKey(mapKey, k);
//			this.lock.readLock().lock();
			if (this.keys.containsKey(mapKey))
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.get(mapKey).remove(key);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
				removalKeys.add(key);
			}
//			this.lock.readLock().unlock();
		}
		
//		this.lock.writeLock().lock();
		this.db.removeAll(keys);
//		this.lock.writeLock().unlock();
		
		this.cache.removeAll(removalKeys);
	}

	public void remove(String mapKey, String k)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, k);
//		this.lock.readLock().lock();
		if (this.keys.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.keys.get(mapKey).remove(key);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();

		if (this.cache.containsKey(key))
		{
			this.cache.remove(key);
		}
		else
		{
//			this.lock.writeLock().lock();
			this.db.remove(key);
//			this.lock.writeLock().unlock();
		}
	}

	public void clear(String mapKey)
	{
		Set<String> rscKeys = null;
//		this.lock.readLock().lock();
		if (this.keys.containsKey(mapKey))
		{
			rscKeys = this.keys.get(mapKey);
			if (rscKeys != null)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.keys.remove(mapKey);
				this.db.removeAll(rscKeys);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();

		if (rscKeys != null)
		{
			this.cache.removeAll(rscKeys);
		}
	}

	public void clear()
	{
		Set<String> rscKeys = null;
//		this.lock.writeLock().lock();
		Set<String> mapKeys = this.keys.keySet();
		for (String mapKey : mapKeys)
		{
			rscKeys = this.keys.get(mapKey);
			if (rscKeys != null)
			{
				this.keys.remove(mapKey);
				this.db.removeAll(rscKeys);
			}
		}
//		this.lock.writeLock().unlock();
		if (rscKeys != null)
		{
			this.cache.removeAll(rscKeys);
		}
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		this.lock.writeLock().lock();
		this.db.save(v);
//		this.lock.writeLock().unlock();
		this.currentEvictedCount.getAndIncrement();
		if (this.currentEvictedCount.get() >= this.alertEvictedCount)
		{
			// When the evicted data count exceeds the threshold, it needs to add a new terminal server to the system. I will do that later. 05/12/2018, Bing Li
			throw new TerminalServerOverflowedException(this.storeKey);
		}
	}

	@Override
	public void forward(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String k, Value v)
	{
		// TODO Auto-generated method stub
		
	}

}
