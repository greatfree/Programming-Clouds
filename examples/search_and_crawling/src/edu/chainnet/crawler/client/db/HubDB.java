package edu.chainnet.crawler.client.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.chainnet.crawler.Hub;
import org.greatfree.util.FileManager;

import com.sleepycat.persist.EntityCursor;

// Created: 04/22/2021, Bing Li
public class HubDB
{
	private File envPath;
	private DBEnv env;
	private HubAccessor accessor;

	public HubDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.RAW_HUB_STORE);
		this.accessor = new HubAccessor(this.env.getEntityStore());
	}
	
	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}

	public void saveHubs(List<Hub> hubs, boolean isAssigned)
	{
		for (Hub hub : hubs)
		{
			this.saveHub(hub, isAssigned);
		}
	}

	public void saveHub(Hub hub, boolean isAssigned)
	{
		this.accessor.getPrimaryKey().put(new HubEntity(hub.getHubKey(), hub.getHubURL(), hub.getHubTitle(), hub.getUpdatingPeriod(), isAssigned));
	}

	public List<Hub> loadRawHubs()
	{
		EntityCursor<HubEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, HubEntity.class).entities();
		List<Hub> hubs = new ArrayList<Hub>();
		for (HubEntity hub : results)
		{
			hubs.add(new Hub(hub.getHubKey(), hub.getHubURL(), hub.getHubTitle(), hub.getUpdatingPeriod()));
		}
		results.close();
		return hubs;
	}

	public List<Hub> loadUnassignedRawHubs()
	{
		EntityCursor<HubEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, HubEntity.class).entities();
		List<Hub> hubs = new ArrayList<Hub>();
		for (HubEntity hub : results)
		{
			if (!hub.isAssigned())
			{
				hubs.add(new Hub(hub.getHubKey(), hub.getHubURL(), hub.getHubTitle(), hub.getUpdatingPeriod()));
			}
		}
		results.close();
		return hubs;
	}
}

