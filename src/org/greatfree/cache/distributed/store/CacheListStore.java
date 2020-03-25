package org.greatfree.cache.distributed.store;

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
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 02/24/2019, Bing Li
abstract class CacheListStore<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, CompoundKeyCreator extends CompoundKeyCreatable<String>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private final String storeKey;
	private CompoundKeyCreator keyCreator;
	private final long cacheSize;
	
	private ListIndexDB listKeysDB;
	private Map<String, List<String>> listKeys;
	
	private AtomicBoolean isDown;
//	private ReentrantReadWriteLock lock;

//	public CacheListStore(String storeKey, Factory factory, String rootPath, int totalStoreSize, int cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	public CacheListStore(String storeKey, Factory factory, String rootPath, long totalStoreSize, long cacheSize, int offheapSizeInMB, int diskSizeInMB, CompoundKeyCreator keyCreator)
	{
		super(factory, rootPath, storeKey, totalStoreSize, offheapSizeInMB, diskSizeInMB, StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(rootPath, storeKey)), true);
		this.storeKey = storeKey;
		this.cacheSize = cacheSize;
		
		this.listKeysDB = new ListIndexDB(CacheConfig.getListKeysCachePath(rootPath, this.storeKey));
		this.listKeys = this.listKeysDB.getAllKeys();

		this.keyCreator = keyCreator;
		this.isDown = new AtomicBoolean(false);
//		this.lock = new ReentrantReadWriteLock();
	}

	protected void dispose() throws InterruptedException
	{
		super.closeAtBase();
//		this.lock.writeLock().lock();
		this.listKeysDB.removeAll();
		this.listKeysDB.putAll(this.listKeys);
		this.listKeysDB.dispose();
		this.isDown.set(true);;
//		this.lock.writeLock().unlock();
	}

	protected void addAt2ndBase(String listKey, Value v)
	{
		String key = this.keyCreator.createCompoundKey(listKey, v.getKey());
		super.putAtBase(key, v);
		
//		ListIndexEntity entity;
//		this.lock.readLock().lock();
		if (!this.listKeys.containsKey(listKey))
		{
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			entity = new ListIndexEntity(listKey, new ArrayList<String>());
//			entity.getKeys().add(key);
//			this.listKeysDB.put(entity);
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

	protected void addAllAt2ndBase(String listKey, List<Value> vs)
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
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.listKeys.put(listKey, new ArrayList<String>());
			for (Value rsc : vs)
			{
//				entity.getKeys().add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
				this.listKeys.get(listKey).add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}
//			this.listKeysDB.put(entity);
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
			}
			*/
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.listKeysDB.put(entity);
			for (Value rsc : vs)
			{
				this.listKeys.get(listKey).add(this.keyCreator.createCompoundKey(listKey, rsc.getKey()));
			}
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
	}

	protected boolean isDownAt2ndBase()
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
	
	protected boolean isEmptyAt2ndBase()
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
	
	protected boolean isEmpty2ndBase(String listKey)
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

	protected boolean isFullAt2ndBase(String listKey)
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
	
	protected int getLeftSizeAt2ndBase(String listKey, int endIndex)
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

	protected boolean isListInStoreAt2ndBase(String listKey)
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
	
	protected Set<String> getListKeysAt2ndBase()
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
	
	protected boolean containsKeyAt2ndBase(String listKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return super.containsKeyAtBase(key);
		}
		return false;
	}

	protected Value getAt2ndBase(String listKey, String rscKey)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
			key = this.keyCreator.createCompoundKey(listKey, rscKey);
		}
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return super.getAtBase(key);
		}
		return null;
	}

	protected Value getAt2ndBase(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
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
//		this.lock.readLock().unlock();
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			return super.getAtBase(key);
		}
		return null;
	}
	
	protected List<Value> getRangeAt2ndBase(String listKey, int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(listKey, i);
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
	
	protected List<Value> getTopAt2ndBase(String listKey, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(listKey, i);
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

	protected List<String> getKeysAt2ndBase(String listKey, int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(listKey, i);
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

	protected int getSizeAt2ndBase(String listKey)
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
	
	protected void removeAt2ndBase(String listKey, int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.listKeys.containsKey(listKey))
		{
//			ListIndexEntity indexes = this.listKeysDB.get(listKey);
//			key = indexes.getKeys().get(index);
//			indexes.getKeys().remove(index);
//			if (indexes.getKeys().size() <= 0)
			if (this.listKeys.get(listKey).size() <= 0)
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
//				this.listKeysDB.remove(listKey);
				this.listKeys.remove(listKey);
//				this.lock.readLock().lock();
//				this.lock.writeLock().unlock();
			}
			else
			{
//				this.lock.readLock().unlock();
//				this.lock.writeLock().lock();
//				this.listKeysDB.put(indexes);
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
	
	protected void removeAt2ndBase(String listKey, int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.removeAt2ndBase(listKey, i);
		}
	}

	protected void clearAt2ndBase(Set<String> listKeys)
	{
		for (String listKey: listKeys)
		{
			this.clearAt2ndBase(listKey);
		}
	}
	
	protected void clearAt2ndBase(String listKey)
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
//			this.listKeysDB.remove(listKey);
			this.listKeys.remove(listKey);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
//		this.lock.readLock().unlock();
		super.removeAllAtBase(keys);
	}
	
	public abstract void evict(String k, Value v) throws TerminalServerOverflowedException;
	public abstract void forward(String k, Value v);
	public abstract void remove(String k, Value v);
	public abstract void expire(String k, Value v);
	public abstract void update(String k, Value v);
	public abstract void shutdown() throws InterruptedException;
}
