package org.greatfree.dsf.cps.cache.terminal.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.db.CachedDBConfig;
import org.greatfree.cache.distributed.terminal.db.PostfetchDB;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.util.Time;

import com.sleepycat.persist.EntityCursor;

// Created: 08/21/2018, Bing Li
public class MyCacheTimingDB extends PostfetchDB<MyCacheTiming>
{
	private MyCacheTimingAccessor accessor;

	public MyCacheTimingDB(String path, String storeName)
	{
		super(path, CachedDBConfig.DB_CACHE_SIZE, CachedDBConfig.LOCK_TIME_OUT, storeName);
		this.accessor = new MyCacheTimingAccessor(this.getEnv().getEntityStore());
	}

	@Override
	public MyCacheTiming get(String key)
	{
		MyCacheTimingEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new MyCacheTiming(entity.getCacheKey(), entity.getKey(), Time.getTime((long)entity.getPoints()));
		}
		return null;
	}

	@Override
	public List<MyCacheTiming> getList(Set<String> keys)
	{
		List<MyCacheTiming> rs = new ArrayList<MyCacheTiming>();
		MyCacheTiming r;
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
	public Map<String, MyCacheTiming> getMap(Set<String> keys)
	{
		Map<String, MyCacheTiming> rs = new HashMap<String, MyCacheTiming>();
		MyCacheTiming r;
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
		EntityCursor<MyCacheTimingEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyCacheTimingEntity.class).entities();
		int count = 0;
		for (MyCacheTimingEntity entry : results)
		{
			entry.getKey();
			count++;
		}
		results.close();
		return count;
	}

	@Override
	public void save(MyCacheTiming value)
	{
		this.accessor.getPrimaryKey().put(new MyCacheTimingEntity(value.getCacheKey(), value.getKey(), value.getPoints()));
	}

	@Override
	public void remove(String key)
	{
		this.getEnv().getEntityStore().getPrimaryIndex(String.class, MyCacheTimingEntity.class).delete(key);
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
