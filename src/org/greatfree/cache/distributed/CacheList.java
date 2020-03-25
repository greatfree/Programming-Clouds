package org.greatfree.cache.distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.cache.local.RootCache;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

// Created: 02/21/2019, Bing Li
public abstract class CacheList<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private ListKeyDB sortedKeysDB;
//	private ReentrantReadWriteLock lock;
	private Map<Integer, String> sortedKeys;

//	public CacheList(Factory factory, String rootPath, String cacheKey, int cacheSize, int offheapSizeInMB, int diskSizeInMB, StringKeyDB db, boolean isAsync)
	public CacheList(Factory factory, String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB, StringKeyDB db, boolean isAsync)
	{
		super(factory, rootPath, cacheKey, cacheSize, offheapSizeInMB, diskSizeInMB, db, isAsync);
		
		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(rootPath, cacheKey));
//		this.lock = new ReentrantReadWriteLock();
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
	}

	@Override
	public void shutdown() throws InterruptedException, IOException
	{
		super.closeAtBase();
//		this.lock.writeLock().lock();
		this.sortedKeysDB.removeAll();
		this.sortedKeysDB.putAll(this.sortedKeys);
		this.sortedKeysDB.close();
//		this.lock.writeLock().unlock();
	}

	/*
	protected long getEmptySize()
	{
		return super.getEmptySizeAtBase();
	}
	
	protected long getLeftSize(int currentIndex)
	{
		return super.getLeftSizeAtBase(currentIndex);
	}
	*/

	protected void addAt2ndBase(Value rsc)
	{
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
			this.sortedKeys.put(0, rsc.getKey());
//			this.lock.writeLock().unlock();
		}
		else
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
//			int currentSize = this.sortedKeysDB.getSize();
//			this.sortedKeysDB.put(currentSize, rsc.getKey());
			this.sortedKeys.put(this.sortedKeys.size(), rsc.getKey());
//			this.lock.writeLock().unlock();
		}
	}

	protected void addAllAt2ndBase(List<Value> rscs)
	{
//		int index = this.sortedKeysDB.getSize();
		for (Value rsc : rscs)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
			this.sortedKeys.put(this.sortedKeys.size(), rsc.getKey());
//			this.lock.writeLock().unlock();
		}
	}

	/*
	protected boolean containsKey(String key)
	{
		return super.containsKeyAtBase(key);
	}

	protected boolean isEmpty()
	{
		return super.isEmptyAtBase();
	}
	*/
	
	protected String getKeyAt2ndBase(int index)
	{
		/*
		this.lock.readLock().lock();
		try
		{
			return this.sortedKeys.get(index);
		}
		finally
		{
			this.lock.readLock().unlock();
		}
		*/
		return this.sortedKeys.get(index);
	}

	protected Value getAt2ndBase(int index)
	{
		String key;
//		this.lock.readLock().lock();
		key = this.sortedKeys.get(index);
//		this.lock.readLock().unlock();
		if (key != null)
		{
			return super.getAtBase(key);
		}
		return null;
	}

	protected List<Value> getRangeAt2ndBase(int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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
	
	protected List<String> getResourceKeysAt2ndBase(int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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

	protected List<Value> getTopAt2ndBase(int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.getAt2ndBase(i);
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
	protected long getMemCacheSize()
	{
		return super.getMemCacheSizeAtBase();
	}
	*/

	protected void removeAt2ndBase(int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
		if (this.sortedKeys.containsKey(index))
		{
			key = this.sortedKeys.get(index);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
			this.sortedKeys.remove(index);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			super.removeAtBase(key);
		}
	}
	
	protected void removeAt2ndBase(int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.removeAt2ndBase(i);
		}
	}

	protected void clearAt2ndBase()
	{
		String key;
//		this.lock.readLock().lock();
		Set<Integer> keys = this.sortedKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
			key = this.sortedKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
			this.sortedKeys.remove(index);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}
	}

	public abstract void evict(String k, Value v) throws TerminalServerOverflowedException;
	public abstract void forward(String k, Value v);
	public abstract void remove(String k, Value v);
	public abstract void expire(String k, Value v);
	public abstract void update(String k, Value v);

}
