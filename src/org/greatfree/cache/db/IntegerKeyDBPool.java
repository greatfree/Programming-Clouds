package org.greatfree.cache.db;

import java.io.IOException;

import org.greatfree.reuse.QueuedPool;

// Created: 05/22/2017, Bing Li
public class IntegerKeyDBPool implements DBPoolable<IntegerKeyDB>
{
	private QueuedPool<IntegerKeyDB, IntegerKeyDBCreator, IntegerKeyDBDisposer> pool;
	
	private IntegerKeyDBPool()
	{
		this.pool = new QueuedPool<IntegerKeyDB, IntegerKeyDBCreator, IntegerKeyDBDisposer>(CachedDBConfig.DB_POOL_SIZE, new IntegerKeyDBCreator(), new IntegerKeyDBDisposer());
	}
	
	private static IntegerKeyDBPool instance = new IntegerKeyDBPool();
	
	public static IntegerKeyDBPool SHARED()
	{
		if (instance == null)
		{
			instance = new IntegerKeyDBPool();
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
	public void dispose(IntegerKeyDB db)
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
	public IntegerKeyDB create(String path)
	{
		try
		{
			return this.pool.create(path);
		}
		catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return CachedDBConfig.NO_INTEGER_KEY_DB;
	}

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		this.pool.setIdleChecker(idleCheckDelay, idleCheckPeriod, maxIdleTime);
	}

	@Override
	public IntegerKeyDB getDB(String path)
	{
		try
		{
			return this.pool.get(path);
		}
		catch (InstantiationException | IllegalAccessException | IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return CachedDBConfig.NO_INTEGER_KEY_DB;
	}

	@Override
	public void collectDB(IntegerKeyDB db)
	{
		// TODO Auto-generated method stub
		
	}

}
