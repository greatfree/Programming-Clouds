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
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.util.Builder;
import org.greatfree.util.FileManager;

/*
 * The locking is updated for the current version. 08/21/2018, Bing Li
 */
/*
 * The class keeps the consistency between the Cache and the StringKeyDB after evicting. If one pair of key/value is eciected,  05/30/2018, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class GenericMap<Key, Value extends Serializable, Factory extends CacheMapFactorable<Key, Value>, DB extends KeyLoadable<Key>> implements CacheEventable<Key, Value>
{
	private final String cacheKey;
	private Cache<Key, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;

	private ConcurrentSkipListSet<Key> keys;
	private DB db;
	
	private CacheListener<Key, Value, GenericMap<Key, Value, Factory, DB>> listener;
	
	private AtomicBoolean isDown;
	
//	private ReentrantReadWriteLock lock;
	
	public GenericMap(Factory factory, String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB, DB db)
	{
		this.cacheKey = cacheKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheKey);
		this.listener = new CacheListener<Key, Value, GenericMap<Key, Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = cacheSize;
		this.db = db;
		this.keys = db.loadKeys();
		
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public GenericMap(PersistableMapBuilder<Key, Value, Factory, DB> builder)
	{
		this.cacheKey = builder.getCacheKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.cacheKey);
		this.listener = new CacheListener<Key, Value, GenericMap<Key, Value, Factory, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		this.cacheSize = builder.getCacheSize();
		this.db = builder.getDB();
		this.keys = this.db.loadKeys();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class PersistableMapBuilder<Key, Value extends Serializable, Factory extends CacheMapFactorable<Key, Value>, DB extends KeyLoadable<Key>> implements Builder<GenericMap<Key, Value, Factory, DB>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		private DB db;
		
		public PersistableMapBuilder()
		{
		}

		public PersistableMapBuilder<Key, Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public PersistableMapBuilder<Key, Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = FileManager.appendSlash(rootPath);
			return this;
		}
		
		public PersistableMapBuilder<Key, Value, Factory, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public PersistableMapBuilder<Key, Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public PersistableMapBuilder<Key, Value, Factory, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public PersistableMapBuilder<Key, Value, Factory, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		public PersistableMapBuilder<Key, Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		@Override
		public GenericMap<Key, Value, Factory, DB> build()
		{
			return new GenericMap<Key, Value, Factory, DB>(this);
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

		public DB getDB()
		{
			return this.db;
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
	
	public String getCacheKey()
	{
		return this.cacheKey;
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

	public void put(Key k, Value v)
	{
		this.cache.put(k, v);
//		this.lock.writeLock().lock();
		this.keys.add(k);
//		this.lock.writeLock().unlock();
	}

	public void putAll(Map<Key, Value> values)
	{
		this.cache.putAll(values);
//		this.lock.writeLock().lock();
		this.keys.addAll(values.keySet());
//		this.lock.writeLock().unlock();
	}
	
	public Value get(Key k)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.get(k);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.get(k);
	}
	
	public Map<Key, Value> get(Set<Key> keys)
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

	public boolean containsKey(Key key)
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
	
	public int getLeftSize(int currentAccessedEndIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.keys.size() - currentAccessedEndIndex - 1;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.keys.size() - currentAccessedEndIndex - 1;
	}
	
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

	public Set<Key> getKeys()
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

	public Map<Key, Value> getValues()
	{
		Set<Key> keys = this.getKeys();
		if (keys != null)
		{
			return this.cache.getAll(keys);
		}
		return null;
	}
	
	public void remove(Set<Key> keys)
	{
		this.cache.removeAll(keys);
//		this.lock.writeLock().lock();
		this.keys.removeAll(keys);
//		this.lock.writeLock().unlock();
	}

	public void remove(Key k)
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
	public void evict(Key key, Value v)
	{
//		this.lock.writeLock().lock();
		this.keys.remove(key);
//		this.lock.writeLock().unlock();
	}

	@Override
	public void forward(Key k, Value v)
	{
	}

	@Override
	public void remove(Key k, Value v)
	{
	}

	@Override
	public void expire(Key k, Value v)
	{
	}

	@Override
	public void update(Key k, Value v)
	{
	}
}
