package org.greatfree.cache.local;

import java.io.Serializable;
import java.util.EnumSet;
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
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 * 
 * The class keeps the consistency between the Cache and the StringKeyDB after evicting. 05/30/2018, Bing Li
 */

// Created: 01/13/2018, Bing Li
public class CacheMap<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>> implements CacheEventable<String, Value>
{
	private final String cacheKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	private ConcurrentSkipListSet<String> keys;
	private CacheListener<String, Value, CacheMap<Value, Factory>> listener;

	private StringKeyDB db;
		
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

	public CacheMap(CacheMapBuilder<Value, Factory> builder)
	{
		this.cacheKey = builder.getCacheKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheKey);
		this.listener = new CacheListener<String, Value, CacheMap<Value, Factory>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		
		this.db = new StringKeyDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey()));
		
		this.keys = this.db.loadKeys();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class CacheMapBuilder<Value extends Serializable, Factory extends CacheMapFactorable<String, Value>> implements Builder<CacheMap<Value, Factory>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		public CacheMapBuilder()
		{
		}

		public CacheMapBuilder<Value, Factory> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public CacheMapBuilder<Value, Factory> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public CacheMapBuilder<Value, Factory> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public CacheMapBuilder<Value, Factory> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public CacheMapBuilder<Value, Factory> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public CacheMapBuilder<Value, Factory> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public CacheMap<Value, Factory> build()
		{
			return new CacheMap<Value, Factory>(this);
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
		this.isDown.set(true);;
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

	public String getCacheKey()
	{
		return this.cacheKey;
	}

	public void put(String k, Value v)
	{
		this.cache.put(k, v);
//		this.lock.writeLock().lock();
		/*
		 * For testing. 04/24/2018, Bing Li
		 */
		/*
		if (this.keys.contains(k))
		{
			AuthorPagesInMap.WWW().incrementConflictedCount();
		}
		*/
		/*
		 * End of testing. 04/24/2018, Bing Li
		 */
		this.keys.add(k);
//		this.lock.writeLock().unlock();
	}

	public void putAll(Map<String, Value> values)
	{
//		this.cache.putAll(values);
		for (Map.Entry<String, Value> entry : values.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
//		this.lock.writeLock().lock();
//		this.keys.addAll(values.keySet());
//		this.lock.writeLock().unlock();
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

	public boolean containsKey(String key)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.containsKey(key);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.containsKey(key);
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

	/*
	public int getLeftSize(int currentAccessedEndIndex)
	{
		this.lock.readLock().lock();
		try
		{
			return this.getSize() - currentAccessedEndIndex - 1;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
	}
	*/
	
	public boolean isCacheFull()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cacheSize <= this.keys.size();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cacheSize <= this.keys.size();
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

	public Map<String, Value> getValues()
	{
//		this.lock.readLock().lock();
//		Set<String> keys = this.keys;
//		this.lock.readLock().unlock();
		if (this.keys != null)
		{
			return this.cache.getAll(this.keys);
		}
		return null;
	}
	
	public void remove(Set<String> keys)
	{
		this.cache.removeAll(keys);
//		this.lock.writeLock().lock();
		this.keys.removeAll(keys);
//		this.lock.writeLock().unlock();
	}

	public void remove(String k)
	{
		this.cache.remove(k);
//		this.lock.writeLock().lock();
		this.keys.remove(k);
//		this.lock.writeLock().unlock();
	}
	
	public void clear()
	{
		this.cache.clear();
//		this.lock.writeLock().lock();
		this.keys.clear();
//		this.lock.writeLock().unlock();
	}

	@Override
	public void evict(String key, Value v)
	{
//		this.lock.writeLock().lock();
		this.keys.remove(key);
//		this.lock.writeLock().unlock();
//		AuthorPagesInMap.WWW().incrementEvictedPages();
		
//		AuthorityTiming page = (AuthorityTiming)v;
//		System.out.println("CacheMap-evict(): key = " + key);
//		System.out.println("CacheMap-evict(): " + page.getKey() + ": " + page.getTitle());
	}

	@Override
	public void forward(String k, Value v)
	{
		// For a distributed cache, the evicted value can be forwarded to a distributed node. 04/22/2018, Bing Li
	}

	@Override
	public void remove(String k, Value v)
	{
//		AuthorPagesInMap.WWW().incrementRemovedPages();
	}

	@Override
	public void expire(String k, Value v)
	{
//		AuthorPagesInMap.WWW().incrementExpiredPages();
	}

	@Override
	public void update(String k, Value v)
	{
//		AuthorPagesInMap.WWW().incrementUpdatedPages();
	}

}
