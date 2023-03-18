package org.greatfree.cache.local.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.greatfree.cache.db.SortedListIndexDB;
import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The locking is updated in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 06/21/2017, Bing Li
public class SortedListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;
	
//	private PersistableMap<String, Double, PointingListPointsFactory, StringKeyDB> points;
//	private PersistableMap<Integer, String, ListKeysFactory, IntegerKeyDB> keys;

//	private PersistableMap<String, PointingListIndexes, PointingListIndexesFactory, StringKeyDB> listIndexes;
	private SortedListIndexDB listIndexesDB;
	private Map<String, SortedListIndexes> listIndexes;
	
	private DescendantComp comp;
	
	private CacheListener<String, Value, SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>> listener;

	private CompoundKeyCreator keyCreator;
//	private final boolean isHash;
	
	private AtomicBoolean isDown;
	
//	private ReentrantReadWriteLock lock;

	private final int sortSize;

//	public PointingPersistableListStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long perCacheSize, long offheapSizeInMB, long diskSizeInMB, CompoundKeyCreator keyCreator, boolean isHash, DescendantComp comp)
	/*
	public SortedListStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long perCacheSize, long offheapSizeInMB, long diskSizeInMB, CompoundKeyCreator keyCreator, DescendantComp comp)
	{
		this.storeKey = storeKey;
		this.manager = factory.createManagerInstance(FileManager.appendSlash(rootPath), storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB);
		this.cache = factory.createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.perCacheSize = perCacheSize;
		this.keyCreator = keyCreator;
//		this.isHash = isHash;

		this.listIndexes = new PointingListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(rootPath, storeKey), CacheConfig.LIST_KEYS));

		this.comp = comp;
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
	}
	*/
	
	public SortedListStore(SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();

		/*
		this.listIndexes = new PersistableMap.PersistableMapBuilder<String, PointingListIndexes, PointingListIndexesFactory, StringKeyDB>()
				.factory(new PointingListIndexesFactory())
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
		
		this.listIndexesDB = new SortedListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();

		this.comp = builder.getComp();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.sortSize = builder.getSortSize();
	}
	
	public static class SortedListStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements Builder<SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>>
	{
		private String storeKey;
		private Factory factory;
//		private int cacheSize;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
//		private boolean isHash;
		
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;
		
		private DescendantComp comp;
		private int sortSize;

		public SortedListStoreBuilder()
		{
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		/*
		public PointingPersistableListStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> isHash(boolean isHash)
		{
			this.isHash = isHash;
			return this;
		}
		*/

		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public SortedListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		@Override
		public SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp> build()
		{
			return new SortedListStore<Value, Factory, CompoundKeyCreator, DescendantComp>(this);
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

		/*
		public boolean isHash()
		{
			return this.isHash;
		}
		*/
		
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
		
		public int getSortSize()
		{
			return this.sortSize;
		}
	}
	
	public void close() throws IOException
	{
		this.manager.close();
//		this.lock.writeLock().lock();
//		this.points.close();
//		this.keys.close();
		
		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		this.listIndexesDB.dispose();
		
		this.isDown.set(true);;
//		this.lock.writeLock().unlock();
//		this.lock = null;
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
	
	public boolean isEmpty(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.listIndexes.get(this.keyCreator.createPrefixKey(listKey, this.isHash)).getPoints().size() > 0;
			if (this.listIndexes.containsKey(listKey))
			{
//				return !(this.listIndexes.get(listKey).getPoints().size() > 0);
				return !(this.listIndexes.get(listKey).getKeySize() > 0);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return !(this.listIndexes.get(listKey).getKeySize() > 0);
		}
		return true;
	}
	
	public boolean isFull(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(listKey);
				return indexes.getPoints().size() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			return indexes.getPoints().size() >= this.cacheSize;
		}
		return false;
	}
	
	public int getLeftSize(String listKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{		
				return this.listIndexes.get(listKey).getPoints().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{		
			return this.listIndexes.get(listKey).getPoints().size() - endIndex;
		}
		return UtilConfig.NO_COUNT;
	}
	
	public boolean isListInStore(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			return this.listIndexes.isExisted(this.keyCreator.createPrefixKey(listKey, this.isHash));
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

	public void add(String listKey, Value rsc)
	{
//		String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
		String key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
		this.cache.put(key, rsc);
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(listKey);
//			this.listIndexes.get(listKey).addKey(0, key);
//			indexes.addKey(0, key);
			indexes.addKey(key);
//			this.listIndexes.get(listKey).setPoints(key, rsc.getPoints());
			indexes.put(key, rsc.getPoints());
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (rsc.getPoints() > indexes.getMinPoints())
			{
//				Map<String, Float> allPoints = indexes.getPoints();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
				allPoints.put(key, rsc.getPoints());
//				allPoints = CollectionSorter.sortDescendantByValue(allPoints);
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
				int index = 0;
//				Set<String> removedKeys = Sets.newHashSet();
				Set<String> removedKeys = new HashSet<String>();
				indexes.clearKeys();
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
//						indexes.addKey(index++, entry.getKey());
//						System.out.println("1) index = " + index + "), value = " + entry.getValue());
						indexes.addKey(entry.getKey());
					}
					else
					{
//						System.out.println("1) obsIndex = " + indexes.getObsSize() + "), value = " + entry.getValue());
						indexes.addObsKey(entry.getKey());
						removedKeys.add(entry.getKey());
					}
					index++;
				}
				
				for (String entry : removedKeys)
				{
					allPoints.remove(entry);
				}
				indexes.setPoints(allPoints);
			}
			else
			{
				if (indexes.getKeySize() < this.sortSize)
				{
//					System.out.println("2) index = " + indexes.getKeySize() + "), value = " + rsc.getPoints());
					indexes.addKey(key);
					indexes.put(key, rsc.getPoints());
				}
				else
				{
//					System.out.println("2) obsIndex = " + indexes.getObsSize() + "), value = " + rsc.getPoints());
					indexes.addObsKey(key);
				}
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}
	
	public void addAll(String listKey, List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
//		String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
		String key;
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		int index = 0;
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
//				this.listIndexes.get(listKey).addKey(index++, key);
//				indexes.addKey(index++, key);
				if (index < this.sortSize)
				{
					indexes.addKey(key);
//					this.listIndexes.get(listKey).setPoints(key, rsc.getPoints());
					indexes.put(key, rsc.getPoints());
				}
				else
				{
					indexes.addObsKey(key);
				}
				index++;
			}
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
//			Map<String, Float> allPoints = indexes.getPoints();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
//				this.cache.put(key, rsc);
//				allPoints.put(rsc.getKey(), rsc.getPoints());
				allPoints.put(key, rsc.getPoints());
			}
//			allPoints = CollectionSorter.sortDescendantByValue(allPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
//			Set<String> removedKeys = Sets.newHashSet();
			Set<String> removedKeys = new HashSet<String>();
			indexes.clearKeys();
			for (Map.Entry<String, Float> entry : sp.entrySet())
			{
				if (index < this.sortSize)
				{
//					indexes.addKey(index++, entry.getKey());
					indexes.addKey(entry.getKey());
				}
				else
				{
					indexes.addObsKey(entry.getKey());
					removedKeys.add(entry.getKey());
				}
				index++;
			}

			for (String entry : removedKeys)
			{
				allPoints.remove(entry);
			}
			indexes.setPoints(allPoints);

//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(listKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public Value get(String listKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
//			return this.cache.get(this.keyCreator.createCompoundKey(cacheKey, rscKey));
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}
	
	public Value get(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(listKey);
				if (index < this.sortSize)
				{
//					System.out.println("1) SortedListStore-get(): index = " + index);
					key = indexes.getKey(index);
				}
				else
				{
//					System.out.println("2) SortedListStore-get(): index = " + index);
					key = indexes.getObsKey(index - this.sortSize);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
			}
			else
			{
				key = indexes.getObsKey(index - this.sortSize);
			}
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
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
	 * For testing only. 02/14/2019, Bing Li
	 */
	public List<String> getObsKey(String listKey)
	{
		SortedListIndexes indexes = this.listIndexes.get(listKey);
		return indexes.getObsoleteKeys();
	}
	
	/*
	 * For testing only. 02/14/2019, Bing Li
	 */
	public String getObsKey(String listKey, int index)
	{
		SortedListIndexes indexes = this.listIndexes.get(listKey);
		return indexes.getObsoleteKeys().get(index);
	}
	
	/*
	 * For testing only. 02/14/2019, Bing Li
	 */
	public Value get(String key)
	{
		return this.cache.get(key);
	}
	
	/*
	 * It is not reasonable to load all of the value from the cache. 08/20/2018, Bing Li
	 */
	/*
	public List<Value> get(String listKey)
	{
		List<String> retrievalKeys = new ArrayList<String>();
		List<Value> rscs = new ArrayList<Value>();
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
//				return this.getTop(listKey, this.listIndexes.get(listKey).getPoints().size() - 1);
				int lastIndex = this.listIndexes.get(listKey).getPoints().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(listKey).getKey(i);
					if (!key.equals(UtilConfig.EMPTY_STRING))
					{
//						rscs.add(this.cache.get(key));
						retrievalKeys.add(key);
					}
				}
//				return rscs;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		for (String key : retrievalKeys)
		{
			rscs.add(this.cache.get(key));
		}
		return rscs;
	}
	*/
	
	/*
	 * Only the sorted data is returned. The obsolete data is remained. Usually, the method is not called frequently. 02/15/2019, Bing Li
	 */
	public List<String> getKeys(String listKey)
	{
		List<String> rscs = new ArrayList<String>();
		List<String> retrievalKeys = new ArrayList<String>();
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
//				return this.getKeys(listKey, 0, this.listIndexes.get(listKey).getPoints().size() - 1);
				int lastIndex = this.listIndexes.get(listKey).getPoints().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(listKey).getKey(i);
					if (!key.equals(UtilConfig.EMPTY_STRING))
					{
//						rscs.add(this.cache.get(key).getKey());
						retrievalKeys.add(key);
					}
				}
//				return rscs;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			int lastIndex = this.listIndexes.get(listKey).getPoints().size() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(listKey).getKey(i);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					retrievalKeys.add(key);
				}
			}
		}
		for (String entry : retrievalKeys)
		{
			rscs.add(this.cache.get(entry).getKey());
		}
		return rscs;
	}
	
	/*
	 * Only the sorted data size is returned. The obsolete data is remained. Usually, the method is not called frequently. 02/15/2019, Bing Li
	 */
	public int getSize(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(listKey))
			{
				return this.listIndexes.get(listKey).getPoints().size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(listKey))
		{
			return this.listIndexes.get(listKey).getPoints().size();
		}
		return UtilConfig.NO_COUNT;
	}
	
	public void remove(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
//					this.cache.remove(key);
					indexes.remove(index);
					indexes.remove(key);
				}
			}
			else
			{
				key = indexes.getObsKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					indexes.removeObsKey(index);
				}
			}
			if (indexes.getPoints().size() <= 0)
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
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			this.cache.remove(key);
		}
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
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
//		String listKey = this.keyCreator.createPrefixKey(listKey);
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			int size = indexes.getPoints().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
//				this.cache.remove(key);
				keys.add(key);
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
		this.cache.removeAll(keys);
	}

	@Override
	public void evict(String k, Value v)
	{
		this.cache.remove(k);
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