package org.greatfree.cache.local.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.TimingListIndexDB;
import org.greatfree.cache.factory.TimingListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Timing;
import org.greatfree.util.UtilConfig;

/*
 * Since timing is transformed to pointing in the type of Long, the cache is the same as SortedListStore. The cache is out of date. 08/24/2018, Bing Li 
 */

// Created: 06/23/2017, Bing Li
public class TimingListStore<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;

//	private PersistableMap<String, TimingListIndexes, TimingListIndexesFactory, StringKeyDB> listIndexes;
	private TimingListIndexDB listIndexesDB;
	private Map<String, TimingListIndexes> listIndexes;

	private DescendantComp comp;

	private CompoundKeyCreator keyCreator;

	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;
	
	private CacheListener<String, Value, TimingListStore<Value, Factory, CompoundKeyCreator, DescendantComp>> listener;
	
	// The variable is designed for testing ONLY. 04/16/2018, Bing Li
//	private int x = 0;

	/*
	public TimingPersistableListStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long perCacheSize, long offheapSizeInMB, long diskSizeInMB, CompoundKeyCreator keyCreator, DescendantComp comp)
	{
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.perCacheSize = perCacheSize;
		this.keyCreator = keyCreator;
		
		this.listIndexes = new PersistableMap.PersistableMapBuilder<String, TimingListIndexes, TimingListIndexesFactory, StringKeyDB>()
				.factory(new TimingListIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(rootPath, CacheConfig.LIST_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.LIST_KEYS))
				.cacheKey(CacheConfig.LIST_KEYS)
				.cacheSize(totalStoreSize)
				.offheapSizeInMB(offheapSizeInMB)
				.diskSizeInMB(diskSizeInMB)
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(rootPath)))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(rootPath, storeKey))))
				.build();

		this.comp = comp;
		
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
		
//		this.x = 1;
	}
	*/

	public TimingListStore(TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());
		this.listener = new CacheListener<String, Value, TimingListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		/*
		this.listIndexes = new PersistableMap.PersistableMapBuilder<String, TimingListIndexes, TimingListIndexesFactory, StringKeyDB>()
				.factory(new TimingListIndexesFactory())
//				.rootPath(CacheConfig.getStoreKeysRootPath(builder.getRootPath(), CacheConfig.LIST_KEYS))
				.rootPath(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS))
				.cacheKey(CacheConfig.LIST_KEYS)
				.cacheSize(builder.getTotalStoreSize())
				.offheapSizeInMB(builder.getOffheapSizeInMB())
				.diskSizeInMB(builder.getDiskSizeInMB())
//				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(builder.getRootPath())))
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getStoreKeysDBPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()))))
				.build();
				*/
		
		this.listIndexesDB = new TimingListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();
		
		this.comp = builder.getComp();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
//		this.x = 1;
//		System.out.println("The Lock is Initialized ......");
	}
	
	public static class TimingPersistableListStoreBuilder<Value extends Timing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements Builder<TimingListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>
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
		
		private DescendantComp comp;

		public TimingPersistableListStoreBuilder()
		{
		}
		
		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> perCacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TimingPersistableListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}

		@Override
		public TimingListStore<Value, Factory, CompoundKeyCreator, DescendantComp> build()
		{
			return new TimingListStore<Value, Factory, CompoundKeyCreator, DescendantComp>(this);
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
		
		public DescendantComp getComp()
		{
			return this.comp;
		}
	}
	
	public void close()
	{
		this.manager.close();
//		this.lock.writeLock().lock();
		
		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		
		this.listIndexesDB.close();
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
//		this.lock = null;
	}
	
	public String getStoreKey()
	{
		return this.storeKey;
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
	
	public boolean isEmpty(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.listIndexes.get(this.keyCreator.createPrefixKey(listKey)).getTimings().size() > 0;
			return this.listIndexes.get(listKey).getTimings().size() > 0;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.get(listKey).getTimings().size() > 0;
	}
	
	public boolean isFull(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String stackKey = this.keyCreator.createPrefixKey(listKey);
			if (this.listIndexes.containsKey(listKey))
			{
				TimingListIndexes indexes = this.listIndexes.get(listKey);
				return indexes.getTimings().size() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			return indexes.getTimings().size() >= this.cacheSize;
		}
		return false;
	}

	public int getLeftSize(String listKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey);
			if (this.listIndexes.containsKey(listKey))
			{		
				return this.listIndexes.get(listKey).getTimings().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{		
			return this.listIndexes.get(listKey).getTimings().size() - endIndex;
		}
		return UtilConfig.NO_COUNT;
	}
	
	public int getMaxIndex(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getTimings().size() - 1;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return this.listIndexes.get(listKey).getTimings().size() - 1;
		}
		return UtilConfig.INIT_INDEX;
	}
	
	public boolean isListInStore(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.listIndexes.isExisted(this.keyCreator.createPrefixKey(listKey));
			return this.listIndexes.containsKey(listKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.containsKey(listKey);
	}

	public Set<String> getListKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.keySet();
	}
	
	public boolean isEmpty()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listIndexes.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listIndexes.isEmpty();
	}

	public void add(String listKey, Value rsc)
	{
		String key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
		this.cache.put(key, rsc);
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(listKey);
//			indexes.addKey(0, key);
			indexes.addKey(key);
			indexes.setTime(key, rsc.getTime());
			this.listIndexes.put(listKey, indexes);
//			this.cache.put(key, rsc);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			if (rsc.getTime().before(indexes.getOldestTime()))
			{
//				Map<String, Date> allTimings = indexes.getTimings();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
				allTimings.put(key, rsc.getTime());
//				allTimings = CollectionSorter.sortDescendantByValue(allTimings);
				Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
				indexes.setTimings(allTimings);
//				int index = 0;
				for (Map.Entry<String, Date> entry : sp.entrySet())
				{
//					indexes.addKey(index++, entry.getKey());
					indexes.addKey(entry.getKey());
				}
			}
			else
			{
				indexes.appendKey(key);
				indexes.setTime(key, rsc.getTime());
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.cache.put(key, rsc);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}
	
	public void add(String listKey, List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		String key;
//		int index = 0;
		/*
		if (this.lock == null)
		{
			System.out.println("the Lock is NULL ...");
			System.out.println("x = " + this.x);
		}
		else
		{
			System.out.println("the Lock is NOT NULL ...");
			System.out.println("x = " + this.x);
		}
		*/
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new TimingListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
//				indexes.addKey(index++, key);
				indexes.addKey(key);
				indexes.setTime(key, rsc.getTime());
			}
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
//			Map<String, Date> allTimings = indexes.getTimings();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Date> allTimings = new HashMap<String, Date>(indexes.getTimings());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
//				allTimings.put(rsc.getKey(), rsc.getTime());
				allTimings.put(key, rsc.getTime());
			}
//			allTimings = CollectionSorter.sortDescendantByValue(allTimings);
			Map<String, Date> sp = CollectionSorter.sortDescendantByValue(allTimings);
			indexes.setTimings(allTimings);
			for (Map.Entry<String, Date> entry : sp.entrySet())
			{
//				indexes.addKey(index++, entry.getKey());
				indexes.addKey(entry.getKey());
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}
	
	public Value get(String listKey, int index)
	{
		String key = null;
//		this.lock.readLock().lock();
//			String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			key = indexes.getKey(index);
			/*
			if (key != null)
			{
				return this.cache.get(key);
			}
			*/
		}
//		this.lock.readLock().unlock();
		if (key != null)
		{
			return this.cache.get(key);
		}
		return null;
	}

	public List<Value> get(String listKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
			else
			{
				break;
			}
		}
		return rscs;
	}

	public List<String> getKeys(String listKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
			else
			{
				break;
			}
		}
		return rscs;
	}

	public List<Value> getTop(String listKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(listKey, i);
			if (rsc != null)
			{
				rscs.add(rsc);
			}
			else
			{
				break;
			}
		}
		return rscs;
	}
	
	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 * 
	public List<Value> get(String listKey)
	{
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey);
			if (this.listIndexes.containsKey(listKey))
			{
				List<Value> rscs = new ArrayList<Value>();
//				return this.getTop(listKey, this.listIndexes.get(listKey).getPoints().size() - 1);
				int lastIndex = this.listIndexes.get(listKey).getTimings().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(listKey).getKey(i);
					if (key != null)
					{
						rscs.add(this.cache.get(key));
					}
				}
				return rscs;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		return null;
	}
	*/
	
	public List<String> getKeys(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey);
			if (this.listIndexes.containsKey(listKey))
			{
//				return this.getKeys(listKey, 0, this.listIndexes.get(listKey).getPoints().size() - 1);
				List<String> rscs = new ArrayList<String>();
				int lastIndex = this.listIndexes.get(listKey).getTimings().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(listKey).getKey(i);
					if (key != null)
					{
						rscs.add(this.cache.get(key).getKey());
					}
				}
				return rscs;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			List<String> rscs = new ArrayList<String>();
			int lastIndex = this.listIndexes.get(listKey).getTimings().size() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(listKey).getKey(i);
				if (key != null)
				{
					rscs.add(this.cache.get(key).getKey());
				}
			}
			return rscs;
		}
		return null;
	}
	
	public int getSize(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey);
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getTimings().size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return this.listIndexes.get(listKey).getTimings().size();
		}
		return UtilConfig.NO_COUNT;
	}
	
	public void remove(String listKey, int index)
	{
//		Set<String> removalKeys = Sets.newHashSet();
		Set<String> removalKeys = new HashSet<String>();
//		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			String key = indexes.getKey(index);
//			this.cache.remove(key);
			removalKeys.add(key);
			indexes.remove(index);
			indexes.remove(key);
			if (indexes.getTimings().size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(listKey, indexes);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		this.cache.removeAll(removalKeys);
	}
	
	public void remove(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(listKey, i);
		}
	}
	
	public void clear(Set<String> listKeys)
	{
		for (String listKey: listKeys)
		{
			this.clear(listKey);
		}
	}

	public void clear(String listKey)
	{
//		Set<String> removalKeys = Sets.newHashSet();
		Set<String> removalKeys = new HashSet<String>();
//		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			TimingListIndexes indexes = this.listIndexes.get(listKey);
			int size = indexes.getTimings().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
//				this.cache.remove(key);
				removalKeys.add(key);
				indexes.remove(i);
				indexes.remove(key);
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.remove(listKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		this.cache.removeAll(removalKeys);
	}

	@Override
	public void evict(String k, Value v)
	{
//		AuthorPages.WWW().incrementEvictedPages();
	}

	@Override
	public void forward(String k, Value v)
	{
//		System.out.println("TimingPersistableListStore-forward(): " + AuthorPages.WWW().getEvictedPageCount() + " pages are evicted ...");
	}

	@Override
	public void remove(String k, Value v)
	{
//		AuthorPages.WWW().incrementRemovedPages();
	}

	@Override
	public void expire(String k, Value v)
	{
//		System.out.println("TimingPersistableListStore-forward(): " + AuthorPages.WWW().getEvictedPageCount() + " pages are expired ...");
//		AuthorPages.WWW().incrementExpiredPages();
	}

	@Override
	public void update(String k, Value v)
	{
//		System.out.println("TimingPersistableListStore-forward(): one data is updated ...");
//		AuthorPages.WWW().incrementUpdatedPages();
	}
}
