package org.greatfree.cache.distributed.terminal;

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
import java.util.concurrent.atomic.AtomicInteger;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CacheListener;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.SortedListIndexDB;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.factory.SortedListIndexes;
import org.greatfree.cache.local.CacheEventable;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

/*
 * The list cache that sorts only a predefined count of data is implemented. When data is saved in the list, each data should have a unique key. It must employ the constructor Pointing(long points) of Pointing. 02/15/2019, Bing Li
 */

/*
 * The cache is just tested. Its primary functions are the same as those of SortedTerminalMapStore. But it has no the map-based functions. 09/04/2018, Bing Li
 */

/*
 * The cache is not tested. But it is a sub-function of SortedTerminalMapStore. So it should be fine. 09/04/2018, Bing Li
 */

/*
 * The version is updated in its locking. But it is not tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Its counterpart is PointingListStore, in which one key might not have the value because of eviction. 05/31/2018, Bing Li
 * 
 * The terminal cache keeps the evicted in a DB. Thus, no data will be lost unless the local preset disk is overloaded. So usually, any keys have their own values strictly. 05/31/2018, Bing Li
 *
 * The cache is usually used to keep data at the terminal server in a distributed storage system, such as MServer. It can be used at a standalone server only if the key is required to match with one value. 05/31/2018, Bing Li
 * 
 */

// Created: 05/15/2018, Bing Li
public class SortedTerminalListStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
//	private final int cacheSize;
	private final long cacheSize;
	
	private SortedListIndexDB listIndexesDB;
	private Map<String, SortedListIndexes> listIndexes;
	
	private DescendantComp comp;
	
	private CacheListener<String, Value, SortedTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>> listener;

	private CompoundKeyCreator keyCreator;
	
	private DB db;

	private AtomicBoolean isDown;
	
//	private ReentrantReadWriteLock lock;

	private final int sortSize;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	public SortedTerminalListStore(SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, builder.getStoreKey());
		this.listener = new CacheListener<String, Value, SortedTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();
		
		this.listIndexesDB = new SortedListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();

		this.comp = builder.getComp();
		this.sortSize = builder.getSortSize();

		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}
	
	public static class SortedTerminalListStoreBuilder<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>, DB extends PostfetchDB<Value>> implements Builder<SortedTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>>
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

		private DB db;
		private int alertEvictedCount;

		public SortedTerminalListStoreBuilder()
		{
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public SortedTerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public SortedTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB> build()
		{
			return new SortedTerminalListStore<Value, Factory, CompoundKeyCreator, DescendantComp, DB>(this);
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
		
		public int getSortSize()
		{
			return this.sortSize;
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
		
		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		this.listIndexesDB.dispose();
		
		this.db.close();
		this.isDown.set(true);
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
			if (this.listIndexes.containsKey(listKey))
			{
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
			if (this.listIndexes.containsKey(listKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(listKey);
				if (indexes != null)
				{
					return indexes.getPoints().size() >= this.cacheSize;
				}
				return false;
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
			if (indexes != null)
			{
				return indexes.getPoints().size() >= this.cacheSize;
			}
			return false;
		}
		return false;
	}
	
	public int getLeftSize(String listKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
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

	public Value getMaxValueResource(String listKey)
	{
		return this.get(listKey, 0);
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
			this.listIndexes.put(listKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			indexes.addKey(key);
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
//				for (Map.Entry<String, Float> entry : allPoints.entrySet())
				for (Map.Entry<String, Float> entry : sp.entrySet())
				{
					if (index < this.sortSize)
					{
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
			}
			else
			{
				if (indexes.getKeySize() < this.sortSize)
				{
					indexes.addKey(key);
					indexes.put(key, rsc.getPoints());
				}
				else
				{
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
				if (index < this.sortSize)
				{
					indexes.addKey(key);
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

	public Value get(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
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
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			Value rsc = this.cache.get(key);
			if (rsc != null)
			{
				return rsc;
			}
			else
			{
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
		}
		return rscs;
	}

	/*
	 * It is not reasonable to load all of the data from a cache. 08/20/2018, Bing Li
	 */
	/*
	public List<Value> get(String listKey)
	{
		if (this.listIndexes.containsKey(listKey))
		{
			List<Value> rscs = new ArrayList<Value>();
			int lastIndex = this.listIndexes.get(listKey).getPoints().size() - 1;
			String key;
			Value rsc;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(listKey).getKey(i);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					rsc = this.cache.get(key);
					if (rsc != null)
					{
						rscs.add(rsc);
					}
					else
					{
						rsc = this.db.get(key);
						if (rsc != null)
						{
							this.cache.put(key, rsc);
							rscs.add(rsc);
						}
					}
				}
			}
			return rscs;
		}
		return null;
	}
	*/

	/*
	 * Only the sorted data is returned. The obsolete data is remained. Usually, the method is not called frequently. 02/15/2019, Bing Li
	 */
	public List<String> getKeys(String listKey)
	{
		List<String> keys = new ArrayList<String>();
		List<String> rscs = new ArrayList<String>();
		String key;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			int lastIndex = this.listIndexes.get(listKey).getPoints().size() - 1;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(listKey).getKey(i);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
					keys.add(key);
				}
			}
		}
//		this.lock.readLock().unlock();
		Value rsc;
		for (String entry : keys)
		{
			rsc = this.cache.get(entry);
			if (rsc != null)
			{
				rscs.add(rsc.getKey());
			}
			else
			{
//				this.lock.readLock().lock();
				rsc = this.db.get(entry);
//				this.lock.readLock().unlock();
				if (rsc != null)
				{
					// To simplify the code, the data loaded from the DB will NOT be put into the cache. The benefit to do that is not so obvious. In addition, the DB itself has the cache capability. My work just focus on designing a wrapper. 05/31/2018, Bing Li
//						this.cache.put(key, rsc);
					rscs.add(rsc.getKey());
				}
			}
		}
		return rscs;
	}

	public int getSize(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
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

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
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
//		this.lock.writeLock().unlock();
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

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void remove(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(listKey, i);
		}
	}

	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void clear(Set<String> listKeys)
	{
		for (String listKey: listKeys)
		{
			this.clear(listKey);
		}
	}
	
	// To be reasonable as a storage, the remove methods can be retained. But it suggests to invoke the methods as few as possible. 06/15/2018, Bing Li
	// It is not necessary to remove data for a terminal of a large-scale storage system. 06/15/2018, Bing Li
	public void clear(String listKey)
	{
//		Set<String> keys = Sets.newHashSet();
		Set<String> keys = new HashSet<String>();
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(listKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(listKey);
			int size = indexes.getPoints().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKey(i);
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
		
//		this.lock.writeLock().lock();
		this.db.removeAll(keys);
//		this.lock.writeLock().unlock();

		this.cache.removeAll(keys);
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
