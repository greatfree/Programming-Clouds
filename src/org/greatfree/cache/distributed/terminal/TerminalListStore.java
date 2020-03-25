package org.greatfree.cache.distributed.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.cache.db.ListIndexDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

// Created: 02/24/2019, Bing Li
public class TerminalListStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private final String storeKey;
	private CompoundKeyCreator keyCreator;
	private final long cacheSize;
	
	private ListIndexDB listKeysDB;
	private Map<String, List<String>> listKeys;

	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

	private DB db;

	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

	public TerminalListStore(TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getStoreKey(), builder.getTotalStoreSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getStoreKey())), true);

		this.storeKey = builder.getStoreKey();
		this.cacheSize = builder.getCacheSize();
		
		this.listKeysDB = new ListIndexDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), this.storeKey));
		this.listKeys = this.listKeysDB.getAllKeys();
		
		this.keyCreator = builder.getKeyCreator();
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
	}

	public static class TerminalListStoreBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>, DB extends PostfetchDB<Value>> implements Builder<TerminalListStore<Value, Factory, CompoundKeyCreator, DB>>
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

		private DB db;
		private int alertEvictedCount;

		public TerminalListStoreBuilder()
		{
		}
		
		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> storeKey(String storeKey)
		{
			this.storeKey = storeKey;
			return this;
		}

		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> keyCreator(CompoundKeyCreator keyCreator)
		{
			this.keyCreator = keyCreator;
			return this;
		}

		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}
		
		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> totalStoreSize(long totalStoreSize)
		{
			this.totalStoreSize = totalStoreSize;
			return this;
		}
		
		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalListStoreBuilder<Value, Factory, CompoundKeyCreator, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalListStore<Value, Factory, CompoundKeyCreator, DB> build() throws IOException
		{
			return new TerminalListStore<Value, Factory, CompoundKeyCreator, DB>(this);
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
		
		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
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
		
		this.db.close();
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
//				ListIndexEntity indexes = this.listKeysDB.get(listKey);
				List<String> indexes = this.listKeys.get(listKey);
				if (indexes != null)
				{
					return indexes.size() >= this.cacheSize;
				}
				return false;
			}
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		if (this.listKeys.containsKey(listKey))
		{
			List<String> indexes = this.listKeys.get(listKey);
			if (indexes != null)
			{
				return indexes.size() >= this.cacheSize;
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

	public Value get(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
//			ListIndexEntity indexes = this.listKeysDB.get(listKey);
			List<String> indexes = this.listKeys.get(listKey);
			key = indexes.get(index);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			Value rsc = super.getAtBase(key);
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

	public Value getMaxValueResource(String listKey)
	{
		return this.get(listKey, 0);
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

	public void add(String listKey, Value rsc)
	{
		String key = this.keyCreator.createCompoundKey(listKey, rsc.getKey());
		super.putAtBase(key, rsc);

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
			List<String> keys = new ArrayList<String>();
			keys.add(key);
			this.listKeys.put(listKey, keys);
			
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			entity = this.listKeysDB.get(listKey);
//			entity.getKeys().add(key);
			List<String> keys = this.listKeys.get(listKey);
			keys.add(key);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.listKeysDB.put(entity);
			this.listKeys.put(listKey, keys);
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
			super.putAtBase(key, entry);
		}
		
//		ListIndexEntity entity;
//		this.lock.readLock().lock();
		if (!this.listKeys.containsKey(listKey))
		{
//			entity = new ListIndexEntity(listKey, new ArrayList<String>());
			List<String> keys = new ArrayList<String>();
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			for (Value rsc : vs)
			{
//				entity.getKeys().add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
				keys.add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}
//			this.listKeysDB.put(entity);
			this.listKeys.put(listKey, keys);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		else
		{
//			entity = this.listKeysDB.get(listKey);
			List<String> keys = this.listKeys.get(listKey);
			for (Value rsc : vs)
			{
//				entity.getKeys().add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
				keys.add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.listKeysDB.put(entity);
			this.listKeys.put(listKey, keys);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	public void remove(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
//			ListIndexEntity indexes = this.listKeysDB.get(listKey);
			List<String> keys = this.listKeys.get(listKey);
//			key = indexes.getKeys().get(index);
			key = keys.get(index);
//			indexes.getKeys().remove(index);
			keys.remove(index);
//			if (indexes.getKeys().size() <= 0)
			if (keys.size() <= 0)
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
				this.listKeys.put(listKey, keys);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			super.removeAtBase(key);
		}
		else
		{
//			this.lock.writeLock().lock();
			this.db.remove(key);
//			this.lock.writeLock().unlock();
		}
	}
	
	protected void remove(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(listKey, i);
		}
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
