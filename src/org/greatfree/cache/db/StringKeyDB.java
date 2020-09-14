package org.greatfree.cache.db;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListSet;

import org.greatfree.cache.KeyLoadable;
import org.greatfree.util.FileManager;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Tools;

import com.sleepycat.persist.EntityCursor;

// Created: 05/22/2017, Bing Li
public class StringKeyDB extends FreeObject implements KeyLoadable<String>
{
	private static final long serialVersionUID = -5729594953287014778L;
	private File envPath;
	private DBEnv env;
	private StringKeyAccessor accessor;
	
	public StringKeyDB(String path)
	{
		super(Tools.getHash(path));
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, CachedDBConfig.CACHED_STRING_KEYS_STORE);
		this.accessor = new StringKeyAccessor(this.env.getEntityStore());
	}
	
	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}

	@Override
	public ConcurrentSkipListSet<String> loadKeys()
	{
		EntityCursor<StringKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, StringKeyEntity.class).entities();
//		Set<String> keys = Sets.newHashSet();
		ConcurrentSkipListSet<String> keys = new ConcurrentSkipListSet<String>();
		for (StringKeyEntity entity : results)
		{
			keys.add(entity.getKey());
		}
		results.close();
		return keys;
	}

	/*
	@Override
	public Set<String> loadKeys(String cacheKey)
	{
		PrimaryIndex<String, StringKeyEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, StringKeyEntity.class);
		SecondaryIndex<String, String, StringKeyEntity> stackKeyIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, SharedDBConfig.CACHE_KEY);
		EntityJoin<String, StringKeyEntity> join = new EntityJoin<String, StringKeyEntity>(primaryIndex);
		join.addCondition(stackKeyIndex, cacheKey);
		ForwardCursor<StringKeyEntity> results = join.entities();

		Set<String> keys = Sets.newHashSet();
		for (StringKeyEntity entity : results)
		{
			keys.add(entity.getKey());
		}
		results.close();
		return keys;
	}
	*/

	@Override
//	public void saveKey(String key, String cacheKey)
	public void saveKey(String key)
	{
//		this.accessor.getPrimaryKey().put(new StringKeyEntity(key, cacheKey));
		this.accessor.getPrimaryKey().put(new StringKeyEntity(key));
	}

	@Override
//	public void saveKeys(Set<String> keys, String cacheKey)
	public void saveKeys(ConcurrentSkipListSet<String> keys)
	{
		for (String key : keys)
		{
//			this.saveKey(key, cacheKey);
			this.saveKey(key);
		}
	}

	@Override
	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, StringKeyEntity.class).delete(key);
	}

	@Override
	public void remove(ConcurrentSkipListSet<String> keys)
	{
		for (String key : keys)
		{
			this.remove(key);
		}
	}

	@Override
	public void removeAll()
	{
		EntityCursor<StringKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, StringKeyEntity.class).entities();
		for (StringKeyEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getKey();
			results.delete();
		}
		results.close();
	}
}
