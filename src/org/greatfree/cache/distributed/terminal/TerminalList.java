package org.greatfree.cache.distributed.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.cache.distributed.CacheList;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.cache.local.CacheMapFactorable;
import org.greatfree.exceptions.TerminalServerOverflowedException;
import org.greatfree.util.Builder;
import org.greatfree.util.UniqueKey;
import org.greatfree.util.UtilConfig;

// Created: 02/21/2019, Bing Li
public class TerminalList<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> extends CacheList<Value, Factory>
{
	private DB db;
	private AtomicInteger currentEvictedCount;
	private final int alertEvictedCount;

//	private ReentrantReadWriteLock lock;

	public TerminalList(TerminalListBuilder<Value, Factory, DB> builder)
	{
		super(builder.getFactory(), builder.getRootPath(), builder.getCacheKey(), builder.getCacheSize(), builder.getOffheapSizeInMB(), builder.getDiskSizeInMB(), StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(builder.getRootPath(), builder.getCacheKey())), true);
		
		this.db = builder.getDB();
		this.currentEvictedCount = new AtomicInteger(this.db.getSize());
		this.alertEvictedCount = builder.getAlertEvictedCount();
//		this.lock = new ReentrantReadWriteLock();
	}
	
	public static class TerminalListBuilder<Value extends UniqueKey, Factory extends CacheMapFactorable<String, Value>, DB extends PostfetchDB<Value>> implements Builder<TerminalList<Value, Factory, DB>>
	{
		private String cacheKey;
		private Factory factory;
		private String rootPath;
//		private int cacheSize;
		private long cacheSize;
		private int offheapSizeInMB;
		private int diskSizeInMB;

		private DB db;
		private int alertEvictedCount;

		public TerminalListBuilder()
		{
		}

		public TerminalListBuilder<Value, Factory, DB> cacheKey(String cacheKey)
		{
			this.cacheKey = cacheKey;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> factory(Factory factory)
		{
			this.factory = factory;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> rootPath(String rootPath)
		{
			this.rootPath = rootPath;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> cacheSize(long cacheSize)
		{
			this.cacheSize = cacheSize;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> offheapSizeInMB(int offheapSizeInMB)
		{
			this.offheapSizeInMB = offheapSizeInMB;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> diskSizeInMB(int diskSizeInMB)
		{
			this.diskSizeInMB = diskSizeInMB;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> db(DB db)
		{
			this.db = db;
			return this;
		}

		public TerminalListBuilder<Value, Factory, DB> alertEvictedCount(int alertEvictedCount)
		{
			this.alertEvictedCount = alertEvictedCount;
			return this;
		}

		@Override
		public TerminalList<Value, Factory, DB> build()
		{
			return new TerminalList<Value, Factory, DB>(this);
		}

		public String getCacheKey()
		{
			return this.cacheKey;
		}
		
		public Factory getFactory()
		{
			return this.factory;
		}
		
		public String getRootPath()
		{
			return this.rootPath;
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

		public DB getDB()
		{
			return this.db;
		}
		
		public int getAlertEvictedCount()
		{
			return this.alertEvictedCount;
		}
	}
	
	public void shutdown() throws InterruptedException, IOException
	{
		super.shutdown();
//		this.lock.writeLock().lock();
		this.db.close();
//		this.lock.writeLock().unlock();
	}
	
	public void add(Value v)
	{
		super.addAt2ndBase(v);
	}
	
	public void addAll(List<Value> vs)
	{
		super.addAllAt2ndBase(vs);
	}
	
	public boolean isEmpty()
	{
		return super.isEmptyAtBase();
	}
	
	public Value get(int index)
	{
		Value v = super.getAt2ndBase(index);
		if (v == null)
		{
			String key = super.getKeyAt2ndBase(index);
			if (!key.equals(UtilConfig.NO_KEY))
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
		return v;
	}
	
	public Value get(String key)
	{
		Value v = super.getAtBase(key);
		if (v == null)
		{
			return this.db.get(key);
		}
		return v;
	}

	public List<Value> getRange(int startIndex, int endIndex)
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
		}
		return rscs;
	}

	public List<String> getKeys(int startIndex, int endIndex)
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
		}
		return rscs;
	}

	public List<Value> getTop(int endIndex)
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
		}
		return rscs;
	}
	
	public void remove(int index)
	{
		super.removeAt2ndBase(index);
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
			throw new TerminalServerOverflowedException(super.getCacheKeyAtBase());
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
