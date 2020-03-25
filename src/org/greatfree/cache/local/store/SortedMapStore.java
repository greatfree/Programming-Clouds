package org.greatfree.cache.local.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
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
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.FileManager;
import org.greatfree.util.Pointing;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 03/07/2019, Bing Li
public class SortedMapStore<Value extends Pointing, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Value>> implements CacheEventable<String, Value>
{
	private final String storeKey;
	private Cache<String, Value> cache;
	private PersistentCacheManager manager;
	private final long cacheSize;

	private SortedListIndexDB listIndexesDB;
	private Map<String, SortedListIndexes> listIndexes;
	
	private DescendantComp comp;
	
	private CacheListener<String, Value, SortedMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>> listener;

	private CompoundKeyCreator keyCreator;
	
	private AtomicBoolean isDown;
	
//	private ReentrantReadWriteLock lock;

	private final int sortSize;

	public SortedMapStore(SortedMapStoreBuilder<Value, Factory, CompoundKeyCreator, DescendantComp> builder)
	{
		this.storeKey = builder.getStoreKey();
		this.manager = builder.getFactory().createManagerInstance(FileManager.appendSlash(builder.getRootPath()), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB());
		this.cache = builder.getFactory().createCache(this.manager, this.storeKey);
		this.listener = new CacheListener<String, Value, SortedMapStore<Value, Factory, CompoundKeyCreator, DescendantComp>>(this);
		this.cache.getRuntimeConfiguration().registerCacheEventListener(this.listener, EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS, EnumSet.of(EventType.CREATED, EventType.EVICTED, EventType.EXPIRED, EventType.REMOVED, EventType.UPDATED));

		this.cacheSize = builder.getCacheSize();
		this.keyCreator = builder.getKeyCreator();

		this.listIndexesDB = new SortedListIndexDB(CacheConfig.getStoreKeysRootPath(CacheConfig.getCachePath(builder.getRootPath(), builder.getStoreKey()), CacheConfig.LIST_KEYS));
		this.listIndexes = this.listIndexesDB.getAllIndexes();

		this.comp = builder.getComp();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.sortSize = builder.getSortSize();
	}

	public static class SortedMapStoreBuilder<Resource extends Pointing, Factory extends CacheMapFactorable<String, Resource>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DescendantComp extends Comparator<Resource>> implements Builder<SortedMapStore<Resource, Factory, CompoundKeyCreator, DescendantComp>>
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
		private int sortSize;

		public SortedMapStoreBuilder()
		{
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> comp(DescendantComp comp)
		{
			this.comp = comp;
			return this;
		}
		
		public SortedMapStoreBuilder<Resource, Factory, CompoundKeyCreator, DescendantComp> sortSize(int sortSize)
		{
			this.sortSize = sortSize;
			return this;
		}

		@Override
		public SortedMapStore<Resource, Factory, CompoundKeyCreator, DescendantComp> build()
		{
			return new SortedMapStore<Resource, Factory, CompoundKeyCreator, DescendantComp>(this);
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
	}
	
	public void close() throws IOException
	{
		this.manager.close();
//		this.lock.writeLock().lock();
		
		this.listIndexesDB.removeAll();
		this.listIndexesDB.putAll(this.listIndexes);
		this.listIndexesDB.dispose();
		
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
	
	public boolean isEmpty(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getPoints().size() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getPoints().size() > 0;
		}
		return true;
	}
	
	public boolean isFull(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(mapKey);
				return indexes.getPoints().size() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			return indexes.getPoints().size() >= this.cacheSize;
		}
		return false;
	}
	
	public Set<String> getMapKeys()
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
	
	public int size()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			Set<String> cacheKeys = this.listIndexes.keySet();
			int size = 0;
			for (String cacheKey : cacheKeys)
			{
				size += this.listIndexes.get(cacheKey).getKeySize();
			}
			return size;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		Set<String> cacheKeys = this.listIndexes.keySet();
		int size = 0;
		for (String cacheKey : cacheKeys)
		{
			size += this.listIndexes.get(cacheKey).getKeySize();
		}
		return size;
	}
	
	public boolean isMapInStore(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getKeySize() > 0;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getKeySize() > 0;
		}
		return false;
	}
	
	public boolean containsKey(String mapKey, String rscKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.cache.containsKey(this.keyCreator.createCompoundKey(mapKey, rscKey));
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.cache.containsKey(this.keyCreator.createCompoundKey(mapKey, rscKey));
	}
	
	public int getSize(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				return this.listIndexes.get(mapKey).getKeySize();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			return this.listIndexes.get(mapKey).getKeySize();
		}
		return UtilConfig.NO_COUNT;
	}

	public int getLeftSize(String mapKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{		
				return this.listIndexes.get(mapKey).getPoints().size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{		
			return this.listIndexes.get(mapKey).getPoints().size() - endIndex;
		}
		return UtilConfig.NO_COUNT;
	}

	public void put(String mapKey, Value rsc)
	{
		String key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
		this.cache.put(key, rsc);
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.addKey(key);
			indexes.put(key, rsc.getPoints());
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			if (rsc.getPoints() > indexes.getMinPoints())
			{
//				Map<String, Float> allPoints = indexes.getPoints();
				// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
				Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
				allPoints.put(key, rsc.getPoints());
//				allPoints = CollectionSorter.sortDescendantByValue(allPoints);
				Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
				int index = 0;
				Set<String> removedKeys = Sets.newHashSet();
				indexes.clearKeys();
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
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}
	
	public void putAll(String mapKey, List<Value> rscs)
	{
		Collections.sort(rscs, this.comp);
		String key;
		for (Value rsc : rscs)
		{
			key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
			this.cache.put(key, rsc);
		}
		int index = 0;
//		this.lock.readLock().lock();
		if (!this.listIndexes.containsKey(mapKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, new SortedListIndexes());
			// It is required to take it out from the cache. Otherwise, the updates cannot be retained in the cache. 06/23/2017, Bing Li
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
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
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
//			Map<String, Float> allPoints = indexes.getPoints();
			// The operation aims to avoid the exception of ConcurrentModificationException. 10/13/2019, Bing Li
			Map<String, Float> allPoints = new HashMap<String, Float>(indexes.getPoints());
			for (Value rsc : rscs)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rsc.getKey());
				allPoints.put(key, rsc.getPoints());
			}
//			allPoints = CollectionSorter.sortDescendantByValue(allPoints);
			Map<String, Float> sp = CollectionSorter.sortDescendantByValue(allPoints);
			Set<String> removedKeys = Sets.newHashSet();
			indexes.clearKeys();
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

//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listIndexes.put(mapKey, indexes);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public Value get(String mapKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
//			return this.cache.get(this.keyCreator.createCompoundKey(cacheKey, rscKey));
			key = this.keyCreator.createCompoundKey(mapKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return this.cache.get(key);
		}
		return null;
	}

	public Map<String, Value> get(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{		
//				Map<String, Value> rscs = new HashMap<String, Value>();
//				List<Resource> rscs = new ArrayList<Resource>();
//				MapKeys keys = this.keys.get(mapKey);
				SortedListIndexes indexes = this.listIndexes.get(mapKey);
//				Set<String> rscKeys = keys.getKeys();
				Set<String> rscKeys = indexes.getPoints().keySet();
				return this.cache.getAll(rscKeys);
			}
			return null;
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{		
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			Set<String> rscKeys = indexes.getPoints().keySet();
			return this.cache.getAll(rscKeys);
		}
		return null;
	}

	public Value get(String mapKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listIndexes.containsKey(mapKey))
			{
				SortedListIndexes indexes = this.listIndexes.get(mapKey);
				if (index < this.sortSize)
				{
					key = indexes.getKey(index);
				}
				else
				{
					key = indexes.getObsKey(index - this.sortSize);
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
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
	
	public List<Value> get(String mapKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
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

	public Value getMaxValueResource(String mapKey)
	{
		return this.get(mapKey, 0);
	}
	
	public List<String> getKeys(String mapKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
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

	public List<Value> getTop(String mapKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(mapKey, i);
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

	public List<Value> getList(String mapKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
//			String listKey = this.keyCreator.createPrefixKey(listKey, this.isHash);
			if (this.listIndexes.containsKey(mapKey))
			{
				List<Value> rscs = new ArrayList<Value>();
//				return this.getTop(listKey, this.listIndexes.get(listKey).getPoints().size() - 1);
				int lastIndex = this.listIndexes.get(mapKey).getPoints().size() - 1;
				String key;
				for (int i = 0; i <= lastIndex; i++)
				{
					key = this.listIndexes.get(mapKey).getKey(i);
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
		*/
		if (this.listIndexes.containsKey(mapKey))
		{
			List<Value> rscs = new ArrayList<Value>();
//			return this.getTop(listKey, this.listIndexes.get(listKey).getPoints().size() - 1);
			int lastIndex = this.listIndexes.get(mapKey).getPoints().size() - 1;
			String key;
			for (int i = 0; i <= lastIndex; i++)
			{
				key = this.listIndexes.get(mapKey).getKey(i);
				if (key != null)
				{
					rscs.add(this.cache.get(key));
				}
			}
			return rscs;
		}
		return null;
	}
	
	public void remove(String mapKey, Set<String> rscKeys)
	{
//		this.lock.writeLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			String key;
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			for (String rscKey : rscKeys)
			{
				key = this.keyCreator.createCompoundKey(mapKey, rscKey);
				this.cache.remove(key);
				indexes.remove(indexes.getIndex(key));
				indexes.remove(key);
				if (indexes.getPoints().size() <= 0)
				{
					this.listIndexes.remove(mapKey);
				}
				else
				{
					this.listIndexes.put(mapKey, indexes);
				}
			}
		}
//		this.lock.writeLock().unlock();
	}
	
	public void remove(String mapKey, String rscKey)
	{
//		this.lock.writeLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			String key = this.keyCreator.createCompoundKey(mapKey, rscKey);
			this.cache.remove(key);
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			indexes.remove(indexes.getIndex(key));
			indexes.remove(key);
			if (indexes.getPoints().size() <= 0)
			{
				this.listIndexes.remove(mapKey);
			}
			else
			{
				this.listIndexes.put(mapKey, indexes);
			}
		}
//		this.lock.writeLock().unlock();
	}

	public void remove(String mapKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listIndexes.containsKey(mapKey))
		{
			SortedListIndexes indexes = this.listIndexes.get(mapKey);
			if (index < this.sortSize)
			{
				key = indexes.getKey(index);
				if (!key.equals(UtilConfig.EMPTY_STRING))
				{
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
				this.listIndexes.remove(mapKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listIndexes.put(mapKey, indexes);
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

	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
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
