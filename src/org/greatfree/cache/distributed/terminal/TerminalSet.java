package org.greatfree.cache.distributed.terminal;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.SerializedKey;

/*
 * The cache is not so useful. If needed, it can be replaced with the map-based caches. 08/30/2018, Bing Li
 */

/*
 * The version is NOT tested in the Clouds project. Only locking is updated. 08/22/2018, Bing Li
 * 
 * Its counterpart is CacheSet, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/27/2018, Bing Li
public class TerminalSet<Value extends SerializedKey, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String cacheKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	private ConcurrentSkipListSet<String> keys;
	private CacheListener<String, Value, TerminalSet<Value, Factory, DB>> listener;
	
	private StringKeyDB keyDB;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

	private DB db;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;
	
	public TerminalSet(TerminalSetBuilder<Value, Factory, DB> builder)
	{
		this.cacheKey = builder.getCacheKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheKey);
		this.listener = new CacheListener<String, Value, TerminalSet<Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		
		this.keyDB = new StringKeyDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey()));
		this.keys = this.keyDB.loadKeys();

		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();

		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}

	public static class TerminalSetBuilder<Value extends SerializedKey, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> implements Builder<TerminalSet<Value, Factory, DB>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DB db;
		private int alertEvictedCount;

		public TerminalSetBuilder()
		{
		}
		
		public TerminalSetBuilder<Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TerminalSetBuilder<Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public TerminalSetBuilder<Value, Factory, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public TerminalSetBuilder<Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public TerminalSetBuilder<Value, Factory, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TerminalSetBuilder<Value, Factory, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalSetBuilder<Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalSetBuilder<Value, Factory, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalSet<Value, Factory, DB> build()
		{
			return new TerminalSet<Value, Factory, DB>(this);
		}
	
		public String getCacheKey()
		{
			return this.cacheKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public long getCacheSize()
		{
			return this.cacheSize;
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
		this.cache.getRuntimeConfiguration().deregisterCacheEventListener(this.listener);
		this.manager.close();
//		this.lock.writeLock().lock();
		this.keyDB.removeAll();
		this.keyDB.saveKeys(this.keys);
		this.keyDB.dispose();
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
	}

	public boolean isDown()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.isDown;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.isDown.get();
	}

	public boolean contains(Value v)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.contains(v.getKey());
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.contains(v.getKey());
	}
	
	public Set<String> getKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys;
	}
	
	public Value get(String key)
	{
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
	
	public Map<String, Value> get(Set<String> keys)
	{
		Map<String, Value> values = this.cache.getAll(keys);
		if (values.size() >= keys.size())
		{
			return values;
		}
		/*
		this.lock.readLock().lock();
		try
		{
			Map<String, Value> loadedValues = this.db.getMap(keys);
			if (loadedValues != null)
			{
				values.putAll(loadedValues);
			}
			return values;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		Map<String, Value> loadedValues = this.db.getMap(keys);
		if (loadedValues != null)
		{
			values.putAll(loadedValues);
		}
		return values;
	}
	
	/*
	 * It is not reasonable to load all of the values from a cache. 08/21/2018, Bing Li
	 */
	/*
	public Map<String, Value> getAll()
	{
		this.lock.readLock().lock();
		try
		{
			Map<String, Value> values = this.cache.getAll(this.keys);
			if (values.size() >= this.keys.size())
			{
				return values;
			}
			Map<String, Value> loadedValues = this.db.getMap(this.keys);
			if (loadedValues != null)
			{
				values.putAll(loadedValues);
			}
			return values;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/

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

	public int getSize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.size();
	}

	public long getEmptySize()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cacheSize - this.keys.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cacheSize - this.keys.size();
	}

	public void add(Value v)
	{
		this.cache.put(v.getKey(), v);
//		this.lock.writeLock().lock();
		this.keys.add(v.getKey());
//		this.lock.writeLock().unlock();
	}
	
	public void addAll(Set<Value> values)
	{
		for (Value v : values)
		{
			this.add(v);
		}
	}
	
	public Map<String, Value> intersect(Set<String> keys)
	{
//		Set<String> intersectedKeys = null;
//		this.lock.readLock().lock();
//		intersectedKeys = Sets.intersection(this.keys, keys);
		Set<String> intersectedKeys = new HashSet<String>(this.keys);
		intersectedKeys.retainAll(keys);
//		this.lock.readLock().unlock();

		Map<String, Value> values = this.cache.getAll(intersectedKeys);
		if (values.size() >= intersectedKeys.size())
		{
			return values;
		}
//			this.lock.readLock().lock();
		Map<String, Value> loadedValues = this.db.getMap(intersectedKeys);
//			this.lock.readLock().unlock();
		if (loadedValues != null)
		{
			values.putAll(loadedValues);
		}
		return values;
	}
	
	public Map<String, Value> difference(Set<String> keys)
	{
//		Set<String> differentKeys = null;
//		this.lock.readLock().lock();
//		differentKeys = Sets.difference(this.keys, keys);
		Set<String> differentKeys = new HashSet<String>(this.keys);
		differentKeys.removeAll(keys);
//		this.lock.readLock().unlock();

		Map<String, Value> values = this.cache.getAll(differentKeys);
		if (values.size() >= differentKeys.size())
		{
			return values;
		}
		Map<String, Value> loadedValues = this.db.getMap(differentKeys);
		if (loadedValues != null)
		{
			values.putAll(loadedValues);
		}
		return values;
	}
	
	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(Value v)
	{
		if (this.cache.containsKey(v.getKey()))
		{
			this.cache.remove(v.getKey());
		}
		else
		{
//			this.lock.writeLock().lock();
			this.db.remove(v.getKey());
//			this.lock.writeLock().unlock();
		}
//		this.lock.writeLock().lock();
		this.keys.remove(v.getKey());
//		this.lock.writeLock().unlock();
	}
	
	public void removeAll(Set<String> keys)
	{
		this.cache.removeAll(keys);
//		this.lock.writeLock().lock();
		this.db.removeAll(keys);
		this.keys.removeAll(keys);
//		this.lock.writeLock().unlock();
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
			throw new TerminalServerOverflowedException(this.cacheKey);
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
