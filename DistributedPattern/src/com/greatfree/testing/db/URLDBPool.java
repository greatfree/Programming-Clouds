package com.greatfree.testing.db;

import java.io.IOException;

import com.greatfree.reuse.QueuedPool;
import com.greatfree.util.FileManager;

/*
 * A singleton of a database pool for URLDB upon QueuedPool. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class URLDBPool implements DBPoolable<URLDB>
{
	// Declare a database pool. 11/25/2014, Bing Li
	private QueuedPool<URLDB, URLDBCreator, URLDBDisposer> pool;

	// Initialize. 11/25/2014, Bing Li
	private URLDBPool()
	{
		this.pool = new QueuedPool<URLDB, URLDBCreator, URLDBDisposer>(DBConfig.DB_POOL_SIZE, new URLDBCreator(), new URLDBDisposer());
	}

	// Define a singleton. 11/25/2014, Bing Li
	private static URLDBPool instance = new URLDBPool();
	
	public static URLDBPool PERSISTENT()
	{
		if (instance == null)
		{
			instance = new URLDBPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Remove the database from the file system. 11/25/2014, Bing Li
	 */
	@Override
	public void removeDB(String path)
	{
		try
		{
			// Shutdown the database. 11/25/2014, Bing Li
			this.pool.shutdown();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		// Remove the directory of the database from the file system. 11/04/2014, Bing Li
		FileManager.removeFiles(path);
	}

	/*
	 * Shutdown the database. 11/25/2014, Bing Li
	 */
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

	/*
	 * Dispose an instance of a database. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(URLDB db)
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

	/*
	 * Create a new instance of URLDB, which is not managed in the pool. 11/25/2014, Bing Li
	 */
	@Override
	public URLDB create(String path)
	{
		try
		{
			return this.pool.create(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return DBConfig.NO_URL_DB;
	}

	/*
	 * Set the idle checking parameters. 11/25/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		this.pool.setIdleChecker(idleCheckDelay, idleCheckPeriod, maxIdleTime);
	}

	/*
	 * Collect an instance of URLDB though the pool. 11/25/2014, Bing Li
	 */
	@Override
	public void collectDB(URLDB db)
	{
		this.pool.collect(db);
	}

	/*
	 * Get an instance of URLDB from the pool. 11/25/2014, Bing Li
	 */
	@Override
	public URLDB getDB(String path)
	{
		try
		{
			return this.pool.get(path);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return DBConfig.NO_URL_DB;
	}

}
