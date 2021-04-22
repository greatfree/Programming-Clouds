package org.greatfree.framework.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.framework.cps.cache.data.MyCachePointing;

import com.sleepycat.persist.EntityCursor;

// Created: 07/24/2018, Bing Li
public class MyCachePointingDB extends PostfetchDB<MyCachePointing>
{
	private MyCachePointingAccessor accessor;

	public MyCachePointingDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyCachePointingAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyCachePointing get(String key)
	{
		MyCachePointingEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyCachePointing(entity.getCacheKey(), entity.getKey(), entity.getPoints());
		}
		return null;
	}

	@Override
	public List<MyCachePointing> getList(Set<String> keys)
	{
		List<MyCachePointing> rs = new ArrayList<MyCachePointing>();
		MyCachePointing r;
		for (String key : keys)
		{
			r = this.get(key);
			if (r != null)
			{
				rs.add(r);
			}
		}
		return rs;
	}

	@Override
	public Map<String, MyCachePointing> getMap(Set<String> keys)
	{
		Map<String, MyCachePointing> rs = new HashMap<String, MyCachePointing>();
		MyCachePointing r;
		for (String key : keys)
		{
			r = this.get(key);
			if (r != null)
			{
				rs.put(r.getKey(), r);
			}
		}
		return rs;
	}

	@Override
	public int getSize()
	{
		EntityCursor<MyCachePointingEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyCachePointingEntity.class).entities();
		int count = 0;
		for (MyCachePointingEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyCachePointing value)
	{
		this.accessor.getPrimaryKey().put(new MyCachePointingEntity(value.getCacheKey(), value.getKey(), value.getPoints()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyCachePointingEntity.class).delete(key);
	}

	@Override
	public void removeAll(Set<String> keys)
	{
		for (String key : keys)
		{
			this.remove(key);
		}
	}

	@Override
	public void dispose()
	{
		this.accessor.dispose();
		this.close();
	}

}
