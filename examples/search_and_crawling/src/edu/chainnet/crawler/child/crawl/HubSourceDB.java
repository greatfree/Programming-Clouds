package edu.chainnet.crawler.child.crawl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.distributed.terminal.db.PostfetchDB;

import com.sleepycat.persist.EntityCursor;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.HubSource;
import edu.chainnet.crawler.client.db.DBConfig;

// Created: 04/24/2021, Bing Li
class HubSourceDB extends PostfetchDB<HubSource>
{
    private HubSourceAccessor accessor;

    public HubSourceDB(String path, String storeName)
    {
        super(path, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, storeName);
        this.accessor = new HubSourceAccessor(this.getEnv().getEntityStore());
    }

    @Override
    public void dispose()
    {
        this.accessor.dispose();
        this.close();
    }

    @Override
    public HubSource get(String key)
    {
        HubSourceEntity entity = this.accessor.getPrimaryKey().get(key);
        if (entity != null)
        {
            return new HubSource(entity.getHubKey(), entity.getHubURL(), entity.getHubTitle(), entity.getPassedTime(), entity.getUpdatingPeriod(), entity.getLastModified(), entity.getLinkKeys(), entity.getHash());
        }
        return CrawlConfig.NO_HUB_SOURCE;
    }

    @Override
    public List<HubSource> getList(Set<String> keys)
    {
        List<HubSource> rs = new ArrayList<HubSource>();
        HubSource r;
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
    public Map<String, HubSource> getMap(Set<String> keys)
    {
        Map<String, HubSource> rs = new HashMap<String, HubSource>();
        HubSource r;
        for (String key : keys)
        {
            r = this.get(key);
            if (r != null)
            {
                rs.put(r.getHubKey(), r);
            }
        }
        return rs;
    }

    @Override
    public int getSize()
    {
        EntityCursor<HubSourceEntity> results = this.getEnv().getEntityStore().getPrimaryIndex(String.class, HubSourceEntity.class).entities();
        int count = 0;
        for (HubSourceEntity entry : results)
        {
            entry.getHubKey();
            count++;
        }
        results.close();
        return count;
    }

    @Override
    public void remove(String key)
    {
        this.getEnv().getEntityStore().getPrimaryIndex(String.class, HubSourceEntity.class).delete(key);
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
    public void save(HubSource value)
    {
        this.accessor.getPrimaryKey().put(new HubSourceEntity(value.getHubKey(), value.getHubURL(), value.getHubTitle(), value.getPassedTime(), value.getUpdatingPeriod(), value.getLastModified(), value.getLinkKeys(), value.getHash()));
    }

}
