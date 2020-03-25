package org.greatfree.cache.db;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListSet;

import org.greatfree.cache.KeyLoadable;
import org.greatfree.util.FileManager;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Tools;

import com.sleepycat.persist.EntityCursor;

// Created: 05/22/2017, Bing Li
public class IntegerKeyDB extends FreeObject implements KeyLoadable<Integer>
{
	private File envPath;
	private DBEnv env;
	private IntegerKeyAccessor accessor;

	public IntegerKeyDB(String path)
	{
		super(Tools.getHash(path));
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, CachedDBConfig.CACHED_INTEGER_KEYS_STORE);
		this.accessor = new IntegerKeyAccessor(this.env.getEntityStore());
	}

	@Override
	public ConcurrentSkipListSet<Integer> loadKeys()
	{
		EntityCursor<IntegerKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, IntegerKeyEntity.class).entities();
		ConcurrentSkipListSet<Integer> keys = new ConcurrentSkipListSet<Integer>();
		for (IntegerKeyEntity entity : results)
		{
			keys.add(entity.getKey());
		}
		results.close();
		return keys;
	}

	/*
	@Override
	public Set<Integer> loadKeys(String cacheKey)
	{
		PrimaryIndex<String, IntegerKeyEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, IntegerKeyEntity.class);
		SecondaryIndex<String, String, IntegerKeyEntity> stackKeyIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, SharedDBConfig.CACHE_KEY);
		EntityJoin<String, IntegerKeyEntity> join = new EntityJoin<String, IntegerKeyEntity>(primaryIndex);
		join.addCondition(stackKeyIndex, cacheKey);
		ForwardCursor<IntegerKeyEntity> results = join.entities();

		Set<Integer> keys = Sets.newHashSet();
		for (IntegerKeyEntity entity : results)
		{
			keys.add(entity.getKey());
		}
		results.close();
		return keys;
	}
	*/

	@Override
//	public void saveKey(Integer key, String cacheKey)
	public void saveKey(Integer key)
	{
//		this.accessor.getPrimaryKey().put(new IntegerKeyEntity(key, cacheKey));
		this.accessor.getPrimaryKey().put(new IntegerKeyEntity(key));
	}

	@Override
//	public void saveKeys(Set<Integer> keys, String cacheKey)
	public void saveKeys(ConcurrentSkipListSet<Integer> keys)
	{
		for (Integer key : keys)
		{
//			this.saveKey(key, cacheKey);
			this.saveKey(key);
		}
	}

	@Override
	public void remove(Integer key)
	{
		this.env.getEntityStore().getPrimaryIndex(Integer.class, IntegerKeyEntity.class).delete(key);
	}

	@Override
	public void remove(ConcurrentSkipListSet<Integer> keys)
	{
		for (Integer key : keys)
		{
			this.remove(key);
		}
	}

	@Override
	public void removeAll()
	{
		EntityCursor<IntegerKeyEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, IntegerKeyEntity.class).entities();
		for (IntegerKeyEntity entity : results)
		{
			// The below line aims to remove the warning messages. It means nothing. 12/22/2015, Bing Li
			entity.getKey();
			results.delete();
		}
		results.close();
	}

	@Override
	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
}
