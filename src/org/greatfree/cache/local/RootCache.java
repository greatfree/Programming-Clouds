package org.greatfree.cache.local;

import java.io.IOException;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
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
import org.greatfree.cache.KeyLoadable;
import org.greatfree.util.FileManager;
import org.greatfree.util.Rand;

/*
 * The locking is updated for the version. 08/21/2018, Bing Li
 * 
 * The below problem is solved. The public method is set to protected. 07/04/2018, Bing Li
 * 
 * One issue for the class: the public methods might be invoked. However, since no locking, it might cause consistency problems. 06/03/2018, Bing Li
 */

// Created: 04/27/2018, Bing Li
public abstract class RootCache<Key, Value extends Serializable, Factory extends CacheMapFactorable<Key, Value>, DB extends KeyLoadable<Key>> implements CacheEventable<Key, Value>
{
	private final String cacheKey;
	private final String cachePath;
	private Cache<Key, Value> cache;
	private PersistentCacheManager manager;
//	private Cache<Integer, Key> keyCache;
//	private PersistentCacheManager keyManager;
//	private Cache<Key, Integer> indexCache;
//	private PersistentCacheManager indexManager;
//	private final int cacheSize;
	private final long cacheSize;
//	private int currentCacheSize;

	private ConcurrentSkipListSet<Key> keys;
	private DB db;
	
//	private MapListener<Key, Value, Factory, DB> listener;
	private CacheListener<Key, Value, RootCache<Key, Value, Factory, DB>> listener;
	
	private AtomicBoolean isDown;
	
//	private ReentrantReadWriteLock lock;
	
//	private KeysRemover<PersistableMap<Key, Value, Factory, DB>> kRemover;
//	private ScheduledThreadPoolExecutor scheduler;
//	private ScheduledFuture<?> keyRemovalTask;
//	private long keyRemovalPeriod;

//	public PersistableMap(String cacheKey, Cache<Key, Value> valueCache, PersistentCacheManager valueManager, Cache<Integer, Key> keyCache, PersistentCacheManager keyManager, Cache<Key, Integer> indexCache, PersistentCacheManager indexManager, int maxCacheSize, DB db)
//	public PersistableMap(String cacheKey, Cache<Key, Value> cache, PersistentCacheManager manager, int cacheSize, DB db)
//	public PersistableMap(Factory factory, String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB, DB db, long keyRemovalPeriod, ScheduledThreadPoolExecutor scheduler)
//	public RootCache(Factory factory, String rootPath, String cacheKey, int cacheSize, int offheapSizeInMB, int diskSizeInMB, DB db, boolean isAsync)
	public RootCache(Factory factory, String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB, DB db, boolean isAsync)
	{
		this.cacheKey = cacheKey;
		this.cachePath = CacheConfig.getCachePath(rootPath, cacheKey);
//		this.manager = factory.createManagerInstance(rootPath, cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB);
//		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath) + cacheKey, cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.cacheKey);
//		this.listener = new MapListener<Key, Value, Factory, DB>(this);
		this.listener = new CacheListener<Key, Value, RootCache<Key, Value, Factory, DB>>(this);
//		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		if (isAsync)
		{
			// I will provide my asynchronous mechanism to process evicted data. In this way, the code is more generic. So the asynchronous option is changed to the synchronous one. 04/28/2018, Bing Li
			this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		}
		else
		{
			// I will provide my asynchronous mechanism to process evicted data. In this way, the code is more generic. So the asynchronous option is changed to the synchronous one. 04/28/2018, Bing Li
			this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.SYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));
		}
//		this.keyCache = keyCache;
//		this.keyManager = keyManager;
//		this.indexCache = indexCache;
//		this.indexManager = indexManager;
		this.cacheSize = cacheSize;
//		this.currentCacheSize = 0;
		/*
		Key key;
		for (int i = 0; i < this.maxCacheSize; i++)
		{
			key = this.keyCache.get(i);
			if (key != null)
			{
				this.currentCacheSize++;
			}
			else
			{
				break;
			}
		}
		*/
		this.db = db;
//		this.keys = db.loadKeys(this.cacheKey);
		this.keys = db.loadKeys();
		
//		this.kRemover = new KeysRemover<PersistableMap<Key, Value, Factory, DB>>(this);
//		this.keyRemovalPeriod = keyRemovalPeriod;
//		this.scheduler = scheduler;
//		this.keyRemovalTask = null;
		
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
	protected void closeAtBase() throws InterruptedException
	{
		this.cache.getRuntimeConfiguration().deregisterCacheEventListener(this.listener);
		this.manager.close();
//		this.lock.writeLock().lock();
		this.db.removeAll();
//		this.db.saveKeys(this.keys, this.cacheKey);
		this.db.saveKeys(this.keys);
		this.db.dispose();
		this.isDown.set(true);
//		this.keyManager.close();
//		this.indexManager.close();
//		this.keyRemovalTask.cancel(true);
//		this.lock.writeLock().unlock();
	}
	
	public abstract void shutdown() throws InterruptedException, IOException;
	
	protected String getCacheKeyAtBase()
	{
		return this.cacheKey;
	}
	
	protected long getCacheSizeAtBase()
	{
		return this.cacheSize;
	}
	
	protected String getCachePathAtBase()
	{
		return this.cachePath;
	}

	/*
	public ReentrantReadWriteLock getLock()
	{
		return this.lock;
	}
	*/
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected boolean isDownAtBase()
	{
		return this.isDown.get();
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
//	public synchronized void put(Key k, Value v) throws CacheOverflowException
	protected void putAtBase(Key k, Value v)
	{
		/*
		if (this.cacheSize > this.keys.size())
		{
			this.keys.add(k);
			this.cache.put(k, v);
		}
		this.isOverflow(this.keys.size());
		*/
		
		this.cache.put(k, v);
//		this.lock.writeLock().lock();
		this.keys.add(k);
//		this.lock.writeLock().unlock();
//		this.isOverflow(this.keys.size());
		
//		this.isOverflow(this.keys.size(), k, v);
		
//		this.keyCache.put(this.currentCacheSize, k);
//		this.indexCache.put(k, this.currentCacheSize);
//		this.currentCacheSize++;
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
//	public void putAll(Map<Key, Value> values) throws CacheOverflowException
	protected void putAllAtBase(Map<Key, Value> values)
	{
//		System.out.println("RootCache-putAll(): " + values.size() + " values are being saved!");
		/*
		for (Map.Entry<Key, Value> entry : values.entrySet())
		{
			this.put(entry.getKey(), entry.getValue());
		}
		*/
		this.cache.putAll(values);
//		this.lock.writeLock().lock();
		this.keys.addAll(values.keySet());
//		this.lock.writeLock().unlock();
//		System.out.println("RootCache-putAll(): " + values.size() + " values are saved!");
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected Value getAtBase(Key k)
	{
		return this.cache.get(k);
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected Map<Key, Value> getAtBase(Set<Key> keys)
	{
		 // The below lines always return the values whose size is equal to the number of keys even though one of the values is null. 08/25/2018, Bing Li
		// The below line might get null value. It should be avoided. 07/21/2018, Bing Li
//		return this.cache.getAll(keys);
		Map<Key, Value> values = new HashMap<Key, Value>();
		for (Key key : keys)
		{
			if (this.cache.containsKey(key))
			{
				values.put(key, this.cache.get(key));
			}
		}
		return values;
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected boolean containsKeyAtBase(Key key)
	{
		return this.cache.containsKey(key);
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected boolean isEmptyAtBase()
	{
		return this.keys.size() <= 0;
	}
	
	/*
	 * The method is used to return a random value from the cache. It is seldom used. In the N3W case, a random human is returned to borrow the logo. 08/27/2019, Bing Li
	 */
	protected Value getRandomValue()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.get(Rand.getRandomSetElement(this.keys));
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.get(Rand.getRandomSetElement(this.keys));
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected long getMemCacheSizeAtBase()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys.size() < this.cacheSize)
			{
				return this.keys.size();
			}
			return this.cacheSize;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys.size() < this.cacheSize)
		{
			return this.keys.size();
		}
		return this.cacheSize;
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected long getEmptySizeAtBase()
	{
//		return this.maxCacheSize - this.currentCacheSize;
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
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected long getLeftSizeAtBase(int currentAccessedEndIndex)
	{
//		return this.size() - currentAccessedEndIndex - 1;
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cacheSize - currentAccessedEndIndex - 1;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cacheSize - currentAccessedEndIndex - 1;
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected boolean isCacheFullAtBase()
	{
//		return this.maxCacheSize <= this.currentCacheSize;
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

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
//	public synchronized List<Key> getKeys()
	protected Set<Key> getKeysAtBase()
	{
//		Set<Key> keys = Sets.newHashSet();
//		List<Key> keys = new ArrayList<Key>();
		/*
		while (this.valueCache.iterator().hasNext())
		{
			keys.add(this.valueCache.iterator().next().getKey());
		}
		*/
		/*
		for (int i = 0; i < this.currentCacheSize; i++)
		{
			keys.add(this.keyCache.get(i));
		}
		return keys;
		*/
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

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected Map<Key, Value> getValuesAtBase()
	{
		/*
		Map<Key, Value> values = new HashMap<Key, Value>();
		org.ehcache.Cache.Entry<Key, Value> entry;
		while (this.valueCache.iterator().hasNext())
		{
			entry = this.valueCache.iterator().next();
			values.put(entry.getKey(), entry.getValue());
		}
		*/
		/*
		Set<Key> keys = Sets.newHashSet();
		for (int i = 0; i < this.currentCacheSize; i++)
		{
			keys.add(this.keyCache.get(i));
		}
		*/
		boolean isNull = false;
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.keys != null)
			{
				isNull = false;
//				return this.cache.getAll(this.keys);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.keys != null)
		{
			isNull = false;
		}
		if (!isNull)
		{
			return this.cache.getAll(this.keys);
		}
		return null;
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected void removeAllAtBase(Set<Key> keys)
	{
		this.cache.removeAll(keys);
//		this.lock.writeLock().lock();
		this.keys.removeAll(keys);
//		this.lock.writeLock().unlock();
		/*
		for (Key k : keys)
		{
			Integer index = this.indexCache.get(k);
			this.keyCache.remove(index);
			this.indexCache.remove(k);
			this.currentCacheSize--;
		}
		*/
	}

	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected void removeAtBase(Key k)
	{
		this.cache.remove(k);
//		this.lock.writeLock().lock();
		this.keys.remove(k);
//		this.lock.writeLock().unlock();
		/*
		Integer index = this.indexCache.get(k);
		this.keyCache.remove(index);
		this.indexCache.remove(k);
		this.currentCacheSize--;
		*/
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected void removeKeyAtBase(Key k)
	{
//		this.lock.writeLock().lock();
		this.keys.remove(k);
//		this.lock.writeLock().unlock();
	}
	
	/*
	 * The reason to put "protected" here aims to force child classes to re-implement the method such that locking can be applied. The root class has no locking. 06/04/2018, Bing Li
	 */
	protected void clearAtBase()
	{
//		this.lock.writeLock().lock();
		this.keys.clear();
//		this.lock.writeLock().unlock();
		this.cache.clear();
		/*
		this.keyCache.clear();
		this.indexCache.clear();
		*/
	}

}
