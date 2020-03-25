package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.QueuedPool;

// Created: 05/22/2017, Bing Li
public class StringKeyDBPool implements DBPoolable<StringKeyDB>
{
	private QueuedPool<StringKeyDB, StringKeyDBCreator, StringKeyDBDisposer> pool;
	
	private StringKeyDBPool()
	{
		this.pool = new QueuedPool<StringKeyDB, StringKeyDBCreator, StringKeyDBDisposer>(CachedDBConfig.DB_POOL_SIZE, new StringKeyDBCreator(), new StringKeyDBDisposer());
	}
	
	private static StringKeyDBPool instance = new StringKeyDBPool();
	
	public static StringKeyDBPool SHARED()
	{
		if (instance == null)
		{
			instance = new StringKeyDBPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	@Override
	public void removeDB(String path)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown()
	{
		try
		{
			this.pool.shutdown();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void dispose(StringKeyDB db)
	{
		try
		{
			this.pool.dispose(db);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public StringKeyDB create(String path)
	{
		try
		{
			return this.pool.create(path);
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return CachedDBConfig.NO_STRING_KEY_DB;
	}

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		this.pool.setIdleChecker(idleCheckDelay, idleCheckPeriod, maxIdleTime);
	}

	@Override
	public StringKeyDB getDB(String path)
	{
		try
		{
			return this.pool.get(path);
		}
		catch (InstantiationException | IllegalAccessException | IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return CachedDBConfig.NO_STRING_KEY_DB;
	}

	public QueuedPool<StringKeyDB, StringKeyDBCreator, StringKeyDBDisposer> getPool()
	{
		return this.pool;
	}

	@Override
	public void collectDB(StringKeyDB db)
	{
		// TODO Auto-generated method stub
		
	}
}
