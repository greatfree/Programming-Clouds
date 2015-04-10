package com.greatfree.testing.db;

import java.io.IOException;

import com.greatfree.reuse.QueuedPool;
import com.greatfree.util.FileManager;

/*
 * A singleton of a database pool for NodeDB upon QueuedPool. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class NodeDBPool implements DBPoolable<NodeDB>
{
	// Declare a database pool. 11/04/2014, Bing Li
	private QueuedPool<NodeDB, NodeDBCreator, NodeDBDisposer> pool;

	// Initialize. 11/04/2014, Bing Li
	private NodeDBPool()
	{
		this.pool = new QueuedPool<NodeDB, NodeDBCreator, NodeDBDisposer>(DBConfig.DB_POOL_SIZE, new NodeDBCreator(), new NodeDBDisposer());
	}

	// Define a singleton. 11/04/2014, Bing Li
	private static NodeDBPool instance = new NodeDBPool();
	
	public static NodeDBPool PERSISTENT()
	{
		if (instance == null)
		{
			instance = new NodeDBPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Remove the database from the file system. 11/04/2014, Bing Li
	 */
	@Override
	public void removeDB(String path)
	{
		try
		{
			// Shutdown the database. 11/04/2014, Bing Li
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
	 * Shutdown the database. 11/04/2014, Bing Li
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
	 * Dispose an instance of a database. 11/04/2014, Bing Li
	 */
	@Override
	public void dispose(NodeDB db)
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
	 * Create a new instance of NodeDB, which is not managed in the pool. 11/04/2014, Bing Li
	 */
	@Override
	public NodeDB create(String path)
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
		return DBConfig.NO_NODE_DB;
	}

	/*
	 * Set the idle checking parameters. 11/04/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime)
	{
		this.pool.setIdleChecker(idleCheckDelay, idleCheckPeriod, maxIdleTime);
	}

	/*
	 * Collect an instance of NodeDB though the pool. 11/04/2014, Bing Li
	 */
	@Override
	public void collectDB(NodeDB db)
	{
		this.pool.collect(db);
	}

	/*
	 * Get an instance of NodeDB from the pool. 11/04/2014, Bing Li
	 */
	@Override
	public NodeDB getDB(String path)
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
		return DBConfig.NO_NODE_DB;
	}
}
