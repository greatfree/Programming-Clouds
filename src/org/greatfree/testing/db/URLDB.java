package org.greatfree.testing.db;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.DBConfig;
import org.greatfree.cache.db.DBEnv;
import org.greatfree.testing.data.URLValue;
import org.greatfree.util.FileManager;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Tools;

import com.sleepycat.persist.EntityCursor;

/*
 * The class implements the manipulations on the object, URLEntity, to save and retrieve in the way supported by the Berkeley DB. It derives from FreeObject such that its instance can be managed by a QueuedPool. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class URLDB extends FreeObject
{
	private static final long serialVersionUID = -96858998286634395L;
	// Declare the instance of File since operations on a file system is required. 11/25/2014, Bing Li
	private File envPath;
	// Declare the instance of DBEnv to set up the required environment. 11/25/2014, Bing Li
	private DBEnv env;
	// Declare the instance of NodeAccessor to manipulate objects. 11/25/2014, Bing Li
	private URLAccessor accessor;

	/*
	 * Initialize the DB. 11/25/2014, Bing Li
	 */
	public URLDB(String path)
	{
		super(Tools.getHash(path));
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.URL_STORE);
		this.accessor = new URLAccessor(this.env.getEntityStore());
	}

	/*
	 * Dispose the DB. 11/25/2014, Bing Li
	 */
	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	/*
	 * Load all of the persisted URLs from the database. 11/25/2014, Bing Li
	 */
	public Map<String, URLValue> loadAllURLs()
	{
		EntityCursor<URLEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, URLEntity.class).entities();
		Map<String, URLValue> urls = new HashMap<String, URLValue>();
		URLValue url;
		for (URLEntity entity : results)
		{
			url = new URLValue(entity.getKey(), entity.getURL(), entity.getUpdatingPeriod());
			urls.put(url.getKey(), url);
		}
		results.close();
		return urls;
	}

	/*
	 * Load the keys of all of the persisted URLs from the database. 11/25/2014, Bing Li
	 */
	public Set<String> loadAllURLKeys()
	{
		EntityCursor<URLEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, URLEntity.class).entities();
//		Set<String> urlKeys = Sets.newHashSet();
		Set<String> urlKeys = new HashSet<String>();
		for (URLEntity entity : results)
		{
			urlKeys.add(entity.getKey());
		}
		results.close();
		return urlKeys;
	}

	/*
	 * Load the count of all of the persisted URLs. 11/25/2014, Bing Li
	 */
	public long loadAllURLCount()
	{
		return this.env.getEntityStore().getPrimaryIndex(String.class, URLEntity.class).entities().count();
	}
	
	/*
	 * Persist a collection of URLs. 11/25/2014, Bing Li
	 */
	public void saveURLs(Map<String, URLValue> urls)
	{
		for (URLValue url : urls.values())
		{
			this.saveURL(url);
		}
	}
	
	/*
	 * Persist a single URL. 11/25/2014, Bing Li
	 */
	public void saveURL(URLValue url)
	{
		this.accessor.getPrimaryIndex().put(new URLEntity(url.getKey(), url.getURL(), url.getUpdatingPeriod()));
	}
}
