package com.greatfree.testing.db;

import com.greatfree.util.FreeObject;

/*
 * The interface consists of methods that are required to implement a singleton of a database pool based on QueuedPool. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public interface DBPoolable<DB extends FreeObject>
{
	// Remove a database. 11/04/2014, Bing Li
	public void removeDB(String path);
	// Shutdown a database. 11/04/2014, Bing Li
	public void shutdown();
	// Dispose a database. 11/04/2014, Bing Li
	public void dispose(DB db);
	// Create a new database. 11/04/2014, Bing Li
	public DB create(String path);
	// Set the parameters for the idle checking. 11/04/2014, Bing Li
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod, long maxIdleTime);
	// Collect a database for reuse. 11/04/2014, Bing Li
	public void collectDB(DB db);
	// Get a database from the pool. 11/04/2014, Bing Li
	public DB getDB(String path);
}
