package edu.chainnet.crawler.child.crawl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalMap;
import org.greatfree.util.Time;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.HubSource;
import edu.chainnet.crawler.client.db.DBConfig;
import edu.chainnet.crawler.Hub;

// Created: 04/24/2021, Bing Li
public class HubRegistry
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.coordinator");

	private TerminalMap<HubSource, HubSourceCacheFactory, HubSourceDB> hubs;

	private HubRegistry()
	{
	}

	private static HubRegistry instance = new HubRegistry();

	public static HubRegistry WWW()
	{
		if (instance == null)
		{
			instance = new HubRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		log.info("Disposing HubRegistry ...");
		Date startTime = Calendar.getInstance().getTime();
		this.hubs.shutdown();
		Date endTime = Calendar.getInstance().getTime();
		log.info("HubRegistry disposed ...");
		log.info("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to dispose HubRegistry!");
	}

	public void init()
	{
		this.hubs = new TerminalMap.TerminalMapBuilder<HubSource, HubSourceCacheFactory, HubSourceDB>()
				.factory(new HubSourceCacheFactory())
				.rootPath(ChildProfile.CRAWL().getCachePath())
				.cacheKey(CrawlCacheConfig.HUB_HANDLE_MAP_CACHE_KEY)
				.cacheSize(CrawlCacheConfig.HUB_HANDLE_MAP_CACHE_SIZE)
				.offheapSizeInMB(CrawlCacheConfig.HUB_HANDLE_MAP_CACHE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(CrawlCacheConfig.HUB_HANDLE_MAP_CACHE_DISK_SIZE_IN_MB)
				.db(new HubSourceDB(CacheConfig.getTerminalDBPath(ChildProfile.CRAWL().getCachePath(), CrawlCacheConfig.HUB_HANDLE_MAP_CACHE_KEY), DBConfig.HUB_SOURCE_TERMINAL_STORE))
				.alertEvictedCount(CrawlCacheConfig.HUB_HANDLE_MAP_EVICTED_COUNT_ALERT)
				.build();

		log.info("The count of hubs received remotely: " + this.hubs.getSize());
	}

	public long getHubCount()
	{
		return this.hubs.getSize();
	}

	public void addHubs(List<Hub> hubValues)
	{
		log.info("The count of hubs received remotely: " + hubValues.size());
		for (Hub entry : hubValues)
		{
			if (!this.hubs.containsKey(entry.getHubKey()))
			{
				log.info("hub = " + entry);
				this.hubs.put(entry.getHubKey(), new HubSource(entry));
			}
		}
	}

	public void incrementTime(String hubKey)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setPassedTime(hub.getPassedTime() + CrawlConfig.CRAWLING_TIMER_PERIOD);
		}
	}

	public void updateLastModified(String hubKey, Date lastModified)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setLastModified(lastModified);
		}
	}

	public void decrementPeriod(String hubKey)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			if (hub.getUpdatingPeriod() > CrawlConfig.UPDATING_OFFSET)
			{
				hub.setUpdatingPeriod(hub.getUpdatingPeriod() - CrawlConfig.UPDATING_OFFSET);
			}
			if (hub.getUpdatingPeriod() < CrawlConfig.MIN_UPDATING_PERIOD)
			{
				hub.setUpdatingPeriod(CrawlConfig.MIN_UPDATING_PERIOD);
			}
		}
	}

	public void setLinkKeys(String hubKey, Set<String> linkKeys)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setLinkKeys(linkKeys);
		}
	}

	public void setHash(String hubKey, String hash)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setHash(hash);
		}
	}

	public void clearPassedTime(String hubKey)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setPassedTime(0);
		}
	}

	public void initHash(String hubKey, Set<String> linkKeys, String hash)
	{
		if (this.hubs.containsKey(hubKey))
		{
			HubSource hub = this.hubs.get(hubKey);
			hub.setLinkKeys(linkKeys);
			hub.setHash(hash);
		}
	}

	public HubSource getHubSource(String hubKey)
	{
		return this.hubs.get(hubKey);
	}

	public HubSource dequeue()
	{
		String hubKey = CrawlStates.CRAWL().take();
		if (!hubKey.equals(CrawlConfig.NO_HUB_KEY))
		{
			return this.hubs.get(hubKey);
		}
		else
		{
			CrawlStates.CRAWL().reset();
			CrawlStates.CRAWL().recharge(this.hubs.getKeys());
			hubKey = CrawlStates.CRAWL().take();
			if (!hubKey.equals(CrawlConfig.NO_HUB_KEY))
			{
				return this.hubs.get(hubKey);
			}
		}
		return CrawlConfig.NO_HUB_SOURCE;
	}

	public HashMap<String, Hub> updateHubs()
	{
		log.info("Updating hubs ...");
		Date startTime = Calendar.getInstance().getTime();
		HashMap<String, Hub> hubValues = new HashMap<String, Hub>();
		Map<String, HubSource> hubs = this.hubs.getValues();
		Hub hubValue;
		for (HubSource hub : hubs.values())
		{
			hubValue = new Hub(hub.getHubKey(), hub.getHubURL(), hub.getHubTitle(), hub.getUpdatingPeriod());
			hubValues.put(hub.getHubKey(), hubValue);
		}
		Date endTime = Calendar.getInstance().getTime();
		log.info("Hubs updated ...");
		log.info("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to update hubs!");
		return hubValues;
	}

}
