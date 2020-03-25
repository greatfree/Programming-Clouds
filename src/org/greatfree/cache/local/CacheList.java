package org.greatfree.cache.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.ListKeyDB;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

/*
 * 
 * 10/09/2019, Bing Li
 * 
 * The cache is upgrade to keep the keys in memory rather than reading/loading from the DBs.
 * 
 * The old approach results in the low performance.
 * 
 * The keys is written into DB when the cache is closed and loaded from DB when the cache is opened.
 * 
 * When data is removed and evicted, inconsistency must occur since I don't expect to spend time on resorting the keys.
 * 
 */

/*
 * This is a cache that does not sort data when data is added in the list. The order is determined by the added sequence only. Temporal data is suitable to be saved in the cache. 02/16/2019, Bing Li
 */

// Created: 02/15/2019, Bing Li
public class CacheList<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>> extends RootCache<String, Value, Factory, StringKeyDB>
{
	private ListKeyDB sortedKeysDB;
//	private ReentrantReadWriteLock lock;
	private Map<Integer, String> sortedKeys;

	public CacheList(CacheListBuilder<Value, Factory> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.sortedKeysDB = new ListKeyDB(CacheConfig.getListKeysCachePath(builder.getRootPath(), builder.getCacheKey()));
//		this.lock = new ReentrantReadWriteLock();
		this.sortedKeys = this.sortedKeysDB.getAllKeys();
	}

	public static class CacheListBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>> implements Builder<CacheList<Value, Factory>>
	{
		private Factory factory;
		private String rootPath;
		private String cacheKey;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		public CacheListBuilder()
		{
		}

		public CacheListBuilder<Value, Factory> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}
		
		public CacheListBuilder<Value, Factory> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public CacheListBuilder<Value, Factory> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}
		
		public CacheListBuilder<Value, Factory> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}
		
		public CacheListBuilder<Value, Factory> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}
		
		public CacheListBuilder<Value, Factory> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}
		
		@Override
		public CacheList<Value, Factory> build()
		{
			return new CacheList<Value, Factory>(this);
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
	
	public long getSize()
	{
		return this.sortedKeys.size();
	}

	public long getEmptySize()
	{
		return super.getEmptySizeAtBase();
	}
	
	public long getLeftSize(int currentIndex)
	{
		return super.getLeftSizeAtBase(currentIndex);
	}

	public void add(Value rsc)
	{
//		System.out.println("\nValue to be added: " + rsc.getKey() + ", " + rsc.getPoints());
		if (super.getMemCacheSizeAtBase() <= 0)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
//			System.out.println("0) index = " + 0 + "), value = " + rsc.getPoints());
//			this.sortedKeys.put(0, rsc.getKey());
			this.sortedKeys.put(0, rsc.getKey());
//			this.obsoleteKeys.put(0, rsc.getKey());
//			this.lock.writeLock().unlock();
		}
		else
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
//			int currentSize = this.sortedKeys.getSize();
//					System.out.println("2) index = " + currentSize + "), value = " + rsc.getPoints());
//			this.sortedKeys.put(currentSize, rsc.getKey());
			this.sortedKeys.put(this.sortedKeys.size(), rsc.getKey());
//			this.lock.writeLock().unlock();
		}
	}

	public void add(List<Value> rscs)
	{
//		int index = this.sortedKeys.getSize();
		for (Value rsc : rscs)
		{
			super.putAtBase(rsc.getKey(), rsc);
//			this.lock.writeLock().lock();
//			this.sortedKeys.put(index++, rsc.getKey());
			this.sortedKeys.put(this.sortedKeys.size(), rsc.getKey());
//			this.lock.writeLock().unlock();
		}

		/*
		this.lock.writeLock().lock();
		int index = this.sortedKeys.getSize();
		for (Value rsc : rscs)
		{
			this.sortedKeys.put(index++, rsc.getKey());
		}
		this.lock.writeLock().unlock();
		*/
	}

	public boolean containsKey(String key)
	{
		return super.containsKeyAtBase(key);
	}

	public boolean isEmpty()
	{
		return super.isEmptyAtBase();
	}

	public Value get(int index)
	{
		String key;
//		this.lock.readLock().lock();
//		key = this.sortedKeys.get(index);
		key = this.sortedKeys.get(index);
//		this.lock.readLock().unlock();
		if (key != null)
		{
			return super.getAtBase(key);
		}
		return null;
	}

	public List<Value> get(int startIndex, int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(i);
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
	
	public List<String> getResourceKeys(int startIndex, int endIndex)
	{
		List<String> rscs = new ArrayList<String>();
		Value rsc;
		for (int i = startIndex; i <= endIndex; i++)
		{
			rsc = this.get(i);
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

	public List<Value> getTop(int endIndex)
	{
		List<Value> rscs = new ArrayList<Value>();
		Value rsc;
		for (int i = 0; i <= endIndex; i++)
		{
			rsc = this.get(i);
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

	public long getMemCacheSize()
	{
		return super.getMemCacheSizeAtBase();
	}

	public void remove(int index)
	{
		String key = UtilConfig.EMPTY_STRING;
//		this.lock.readLock().lock();
//		if (this.sortedKeys.containsKey(index))
		if (this.sortedKeys.containsKey(index))
		{
//			key = this.sortedKeys.get(index);
			key = this.sortedKeys.get(index);
			
//			this.lock.readLock().unlock();
//			this.lock.writeLock().lock();
//			this.sortedKeys.remove(index);
			this.sortedKeys.remove(index);
//			this.lock.readLock().lock();
//			this.lock.writeLock().unlock();
		}
		if (!key.equals(UtilConfig.EMPTY_STRING))
		{
			super.removeAtBase(key);
		}
	}
	
	public void remove(int startIndex, int endIndex)
	{
		for (int i = startIndex; i <= endIndex; i++)
		{
			this.remove(i);
		}
	}

	public void clear()
	{
		String key;
//		this.lock.readLock().lock();
		Set<Integer> keys = this.sortedKeys.keySet();
//		this.lock.readLock().unlock();
		
		for (Integer index : keys)
		{
//			this.lock.readLock().lock();
//			key = this.sortedKeys.get(index);
			key = this.sortedKeys.get(index);
//			this.lock.readLock().unlock();

//			this.lock.writeLock().lock();
//			this.sortedKeys.remove(index);
			this.sortedKeys.remove(index);
//			this.lock.writeLock().unlock();

			super.removeAtBase(key);
		}
		this.sortedKeys.clear();
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
