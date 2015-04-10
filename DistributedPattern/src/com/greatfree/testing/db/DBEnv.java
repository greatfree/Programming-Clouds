package com.greatfree.testing.db;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

/*
 * The class keeps some required configurations to set up the object-oriented database, the Berkeley DB. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class DBEnv
{
	// Declare the parameters that are required to set up the object-oriented database. 11/03/2014, Bing Li
	private Environment env;
	private EntityStore store;

	/*
	 * Initialize. 11/03/2014, Bing Li
	 */
	public DBEnv(File envHome, boolean readOnly, long cacheSize, long timeout, String storeID)
	{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setCacheSize(cacheSize);
		
		envConfig.setLockTimeout(timeout, TimeUnit.MILLISECONDS);
		StoreConfig storeConfig = new StoreConfig();
		
		envConfig.setReadOnly(readOnly);
		storeConfig.setReadOnly(readOnly);
		
		envConfig.setAllowCreate(!readOnly);
		storeConfig.setAllowCreate(!readOnly);
		
		this.env = new Environment(envHome, envConfig);
		this.store = new EntityStore(this.env, storeID, storeConfig);
	}

	/*
	 * Expose the relevant attribute. 11/03/2014, Bing Li
	 */
	public EntityStore getEntityStore()
	{
		return this.store;
	}
	
	/*
	 * Expose the relevant attribute. 11/03/2014, Bing Li
	 */
	public Environment getEnv()
	{
		return this.env;
	}

	/*
	 * Close the environment. 11/03/2014, Bing Li
	 */
	public void close()
	{
		if (this.store != null)
		{
			try
			{
				this.store.close();
			}
			catch (DatabaseException dbe)
			{
				dbe.printStackTrace();
			}
		}
		
		if (this.env != null)
		{
			try
			{
				this.env.close();
			}
			catch (DatabaseException dbe)
			{
				dbe.printStackTrace();
			}
		}
	}
}
