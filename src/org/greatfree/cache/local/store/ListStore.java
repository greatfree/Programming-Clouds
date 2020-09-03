package org.greatfree.cache.local.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.ListIndexDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 02/20/2019, Bing Li
// public class CacheListStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>> extends RootCache<String, Value, Factory, StringKeyDB>
public class ListStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private final String storeKey;
	private CompoundKeyCreator keyCreator;
//	private final int cacheSize;
	private final long cacheSize;
	
	private ListIndexDB listKeysDB;
	private Map<String, List<String>> listKeys;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

//	public CacheListStore(String storeKey, Factory factory, String rootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB, StringKeyDB db, CompoundKeyCreator keyCreator)
	/*
	public CacheListStore(String storeKey, Factory factory, String rootPath, int cacheSize, long offheapSizeInMB, long diskSizeInMB, CompoundKeyCreator keyCreator)
	{
		super(factory, rootPath, storeKey, cacheSize, offheapSizeInMB, diskSizeInMB, StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(rootPath, storeKey)), true);
		this.storeKey = storeKey;
		
		this.listKeys = new ListIndexDB(CacheConfig.getListKeysCachePath(rootPath, this.storeKey));
		this.keyCreator = keyCreator;
		this.isDown = false;
		this.lock = new ReentrantReadWriteLock();
	}
	*/
	
//	public CacheListStore(CacheListStoreBuilder<Value, Factory> builder)
	public ListStore(CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getStoreKey())), true);
		this.storeKey = builder.getStoreKey();
		this.cacheSize = builder.getCacheSize();
		
		this.listKeysDB = new ListIndexDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), this.storeKey));
		this.listKeys = this.listKeysDB.getAllKeys();
		
		this.keyCreator = builder.getKeyCreator();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}
	
//	public static class CacheListStoreBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>> implements Builder<CacheListStore<Value, Factory>>
	public static class CacheListStoreBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>> implements Builder<ListStore<Value, Factory, CompoundKeyCreator>>
	{
		private String storeKey;
		private Factory factory;
//		private int cacheSize;
		private long cacheSize;
		private CompoundKeyCreator keyCreator;
		
		private String rootPath;
//		private int totalStoreSize;
		private long totalStoreSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		public CacheListStoreBuilder()
		{
		}
		
		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public CacheListStoreBuilder<Value, Factory, CompoundKeyCreator> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		@Override
		public ListStore<Value, Factory, CompoundKeyCreator> build()
		{
			return new ListStore<Value, Factory, CompoundKeyCreator>(this);
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
	}

	@Override
	public void shutdown() throws InterruptedException, IOException
	{
		super.closeAtBase();
//		this.lock.writeLock().lock();
		
		this.listKeysDB.removeAll();
		this.listKeysDB.putAll(this.listKeys);
		this.listKeysDB.dispose();
		
		this.isDown.set(true);
//		this.lock.writeLock().unlock();
	}

	public void add(String listKey, Value v)
	{
		String key = this.keyCreator.createCompoundKey(listKey, v.getKey());
//		String key = StoreEntryKeyCreator.getKey(listKey, v.getKey());
		super.putAtBase(key, v);
		
//		ListIndexEntity entity;
//		this.lock.readLock().lock();
		if (!this.listKeys.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			/*
			entity = new ListIndexEntity(listKey, new ArrayList<String>());
			entity.getKeys().add(key);
			this.listKeysDB.put(entity);
			*/
			
			this.listKeys.put(listKey, new ArrayList<String>());
			this.listKeys.get(listKey).add(key);
			
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			entity = this.listKeysDB.get(listKey);
//			entity.getKeys().add(key);
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.listKeysDB.put(entity);
			this.listKeys.get(listKey).add(key);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public void addAll(String listKey, List<Value> vs)
	{
		String key;
		for (Value entry : vs)
		{
			key = this.keyCreator.createCompoundKey(listKey, entry.getKey());
//			key = StoreEntryKeyCreator.getKey(listKey, entry.getKey());
			super.putAtBase(key, entry);
		}
		
//		ListIndexEntity entity;
//		this.lock.readLock().lock();
		if (!this.listKeys.containsKey(listKey))
		{
//			entity = new ListIndexEntity(listKey, new ArrayList<String>());
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			/*
			for (Value rsc : vs)
			{
				entity.getKeys().add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
//				entity.getKeys().add(StoreEntryKeyCreator.getKey(listKey, rsc.getKey()));
			}
			this.listKeysDB.put(entity);
			*/
			this.listKeys.put(listKey, new ArrayList<String>());
			for (Value rsc : vs)
			{
				this.listKeys.get(listKey).add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}

//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
			/*
			entity = this.listKeysDB.get(listKey);
			for (Value rsc : vs)
			{
				entity.getKeys().add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
//				entity.getKeys().add(StoreEntryKeyCreator.getKey(listKey, rsc.getKey()));
			}
			*/
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			for (Value rsc : vs)
			{
				this.listKeys.get(listKey).add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
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
			return this.listKeys.isEmpty();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listKeys.isEmpty();
	}
	
	public boolean isEmpty(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listKeys.containsKey(listKey))
			{
				return !(this.listKeys.get(listKey).size() > 0);
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{
			return !(this.listKeys.get(listKey).size() > 0);
		}
		return true;
	}

	public boolean isFull(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listKeys.containsKey(listKey))
			{
				return this.listKeys.get(listKey).size() >= this.cacheSize;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{
			return this.listKeys.get(listKey).size() >= this.cacheSize;
		}
		return false;
	}
	
	public int getLeftSize(String listKey, int endIndex)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listKeys.containsKey(listKey))
			{		
				return this.listKeys.get(listKey).size() - endIndex;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{		
			return this.listKeys.get(listKey).size() - endIndex;
		}
		return UtilConfig.NO_COUNT;
	}

	
	public boolean isListInStore(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listKeys.containsKey(listKey);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listKeys.containsKey(listKey);
	}
	
	public boolean isExisted(String listKey, String rscKey)
	{
		if (this.listKeys.containsKey(listKey))
		{
			return super.containsKeyAtBase(this.keyCreator.createCompoundKey(listKey, rscKey));
		}
		return false;
	}
	
	public Set<String> getListKeys()
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.listKeys.keySet();
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.listKeys.keySet();
	}

	public Value get(String listKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
//			key = StoreEntryKeyCreator.getKey(listKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return super.getAtBase(key);
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
			if (this.listKeys.containsKey(listKey))
			{
				try
				{
					key = this.listKeys.get(listKey).get(index);
				}
				catch (IndexOutOfBoundsException e)
				{
					key = UtilConfig.EMPTY_STRING;
				}
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{
			try
			{
				key = this.listKeys.get(listKey).get(index);
			}
			catch (IndexOutOfBoundsException e)
			{
				key = UtilConfig.EMPTY_STRING;
			}
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return super.getAtBase(key);
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
	
	public List<String> getTopKeys(String listKey, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
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

	public int getSize(String listKey)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			if (this.listKeys.containsKey(listKey))
			{
				return this.listKeys.get(listKey).size();
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{
			return this.listKeys.get(listKey).size();
		}
		return UtilConfig.NO_COUNT;
	}
	
	public void remove(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
//			ListIndexEntity indexes = this.listKeysDB.get(listKey);
//			key = indexes.getKeys().get(index);
//			indexes.getKeys().remove(index);
			if (this.listKeys.get(listKey).size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listKeys.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
				this.listKeys.get(listKey).remove(index);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			super.removeAtBase(key);
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
		Set<String> keys = Sets.newHashSet();
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
			/*
			ListIndexEntity indexes = this.listKeysDB.get(listKey);
			int size = indexes.getKeys().size() - 1;
			String key;
			for (int i = 0; i <= size; i++)
			{
				key = indexes.getKeys().get(i);
				keys.add(key);
				indexes.getKeys().remove(i);
			}
			*/
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listKeys.remove(listKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		super.removeAllAtBase(keys);
	}
	
	@Override
	public void evict(String k, Value v) throws TerminalServerOverflowedException
	{
		super.removeAtBase(k);
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
