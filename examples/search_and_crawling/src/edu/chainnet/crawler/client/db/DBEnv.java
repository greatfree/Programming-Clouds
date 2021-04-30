package edu.chainnet.crawler.client.db;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.sleepycat.je.CacheMode;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

// Created: 04/22/2021, Bing Li
class DBEnv
{
	private Environment env;
	private EntityStore store;

	public DBEnv(File envHome, boolean isReadOnly, long cacheSize, long timeout, String storeID)
	{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		// The cache configuration is important. 05/07/2018, Bing Li
		envConfig.setCacheMode(CacheMode.DEFAULT);
		envConfig.setCacheSize(cacheSize);
		
		envConfig.setLockTimeout(timeout, TimeUnit.MILLISECONDS);
		StoreConfig storeConfig = new StoreConfig();
		
		envConfig.setReadOnly(isReadOnly);
		storeConfig.setReadOnly(isReadOnly);
		
		envConfig.setAllowCreate(!isReadOnly);
		storeConfig.setAllowCreate(!isReadOnly);
		
		this.env = new Environment(envHome, envConfig);
		this.store = new EntityStore(this.env, storeID, storeConfig);
	}

	public EntityStore getEntityStore()
	{
		return this.store;
	}
	
	public Environment getEnv()
	{
		return this.env;
	}
	
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
