package org.greatfree.cache.local;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;
import org.greatfree.util.SerializedKey;

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */

/*
 * The class keeps the consistency between the Cache and the StringKeyDB after evicting. 05/30/2018, Bing Li
 */

/*
 * The cache set does not implement all of the operations of Set. When one operation is needed, I will implement it. 05/24/2018, Bing Li
 */

// Created: 05/24/2018, Bing Li
public class CacheSet<Value extends SerializedKey, Factory extends CacheMapFactorable<String, Value>> implements CacheEventable<String, Value>
{
	private final String cacheKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	private ConcurrentSkipListSet<String> keys;
	private CacheListener<String, Value, CacheSet<Value, Factory>> listener;
	
	private StringKeyDB db;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;
	
	public CacheSet(CacheSetBuilder<Value, Factory> builder)
	{
		this.cacheKey = builder.getCacheKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheKey);
		this.listener = new CacheListener<String, Value, CacheSet<Value, Factory>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		
		this.db = new StringKeyDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey()));
		
		this.keys = this.db.loadKeys();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class CacheSetBuilder<Value extends SerializedKey, Factory extends CacheMapFactorable<String, Value>> implements Builder<CacheSet<Value, Factory>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		public CacheSetBuilder()
		{
		}
		
		public CacheSetBuilder<Value, Factory> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public CacheSetBuilder<Value, Factory> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public CacheSetBuilder<Value, Factory> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public CacheSetBuilder<Value, Factory> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public CacheSetBuilder<Value, Factory> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public CacheSetBuilder<Value, Factory> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public CacheSet<Value, Factory> build()
		{
			return new CacheSet<Value, Factory>(this);
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
		}
		
		public String getCacheKey()
		{
			return this.cacheKey;
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
	}

	public void close()
	{
		this.cache.getRuntimeConfiguration().deregisterCacheEventListener(this.listener);
		this.manager.close();

//		this.lock.writeLock().lock();
		this.db.removeAll();
		this.db.saveKeys(this.keys);
		this.db.dispose();
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
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.get(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.get(key);
	}
	
	public Map<String, Value> get(Set<String> keys)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.getAll(keys);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.getAll(keys);
	}
	
	public Map<String, Value> getAll()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.getAll(this.keys);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.getAll(this.keys);
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
//		this.lock.readLock().lock();
//		Set<String> retrievalKeys = Sets.intersection(this.keys, keys);
		Set<String> retrievalKeys = new HashSet<String>(this.keys);
		retrievalKeys.retainAll(keys);
//		this.lock.readLock().unlock();
		return this.cache.getAll(retrievalKeys);
//		Set<String> intersectedKeys = Sets.intersection(this.keys, keys);
//		Set<String> removedKeys = Sets.difference(this.keys, intersectedKeys);
//		this.cache.removeAll(removedKeys);
//		this.keys = intersectedKeys;
	}
	
	public Map<String, Value> difference(Set<String> keys)
	{
		/*
		this.lock.writeLock().lock();
		Set<String> leftKeys = Sets.difference(this.keys, keys);
		Set<String> removedKeys = Sets.difference(this.keys, leftKeys);
		this.cache.removeAll(removedKeys);
		this.keys = leftKeys;
		this.lock.writeLock().unlock();
		*/
//		this.lock.readLock().lock();
//		Set<String> retrievalKeys = Sets.difference(this.keys, keys);
		Set<String> retrievalKeys = new HashSet<String>(this.keys);
		retrievalKeys.removeAll(keys);
//		this.lock.readLock().unlock();
		return this.cache.getAll(retrievalKeys);
	}
	
	public void remove(Value v)
	{
		this.cache.remove(v.getKey());
//		this.lock.writeLock().lock();
		this.keys.remove(v.getKey());
//		this.lock.writeLock().unlock();
	}
	
	public void removeAll(Set<String> keys)
	{
		this.cache.removeAll(keys);
//		this.lock.writeLock().lock();
		this.keys.removeAll(keys);
//		this.lock.writeLock().unlock();
	}

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
//		this.lock.writeLock().lock();
		this.keys.remove(k);
//		this.lock.writeLock().unlock();
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
