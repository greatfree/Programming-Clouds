package com.greatfree.testing.crawlserver;

import java.util.Date;

import com.greatfree.testing.data.URLValue;
import com.greatfree.testing.db.URLEntity;
import com.greatfree.util.Time;

/*
 * This is the URL that must be crawled by the sample crawler. The hub indicates that the URL is super on the Web. For the detailed explanation of the concept of hub URL, refer to the knowledge of WWW. 11/21/2014, Bing Li
 */

// Created: 11/21/2014, Bing Li
public class HubURL
{
	// The key to represent the URL. The key is unique for each URL. It is the hash string generated upon the URL. 11/23/2014, Bing Li
	private String key;
	// The URL to be crawled. 11/23/2014, Bing Li
	private String url;
	// The time passed after a request is sent to the URL. It is an indicator whether the server on which the URL resides is fast or slow. 11/23/2014, Bing Li
	private long passedTime;
	// The updating period of the URL. According to it, the crawler can determine the moment to crawl it. 11/23/2014, Bing Li
	private long updatingPeriod;
	// The time that the content of the URL was modified. 11/23/2014, Bing Li
	private Date lastModified;

	/*
	 * Initialize the URL. 11/23/2014, Bing Li
	 */
	public HubURL(String key, String url, long updatingPeriod)
	{
		this.key = key;
		this.url = url;
		this.passedTime = 0;
		this.updatingPeriod = updatingPeriod;
		this.lastModified = Time.NO_TIME;
	}

	/*
	 * Dispose the URL. 11/23/2014, Bing Li
	 */
	public void dispose()
	{
		this.lastModified = null;
	}

	/*
	 * Update the instance of the URL. The properties, passedTime, updatingPeriod and lastModified, must change during the crawling procedure. Thus, it is required to update them when new values are detected. 11/23/2014, Bing Li
	 */
	public void update(long passedTime, long updatingPeriod, Date lastModified)
	{
		this.passedTime = passedTime;
		this.updatingPeriod = updatingPeriod;
		this.lastModified = lastModified;
	}

	/*
	 * Get the URLValueEntity to be persisted. 11/23/2014, Bing Li
	 */
	public URLEntity getEntity()
	{
		return new URLEntity(this.key, this.url, this.updatingPeriod);
	}

	/*
	 * Get the value of the URL. The instance of URLValue must be transferred over the network. Thus, it is serialized and the logic to process is filtered. 11/23/2014, Bing Li
	 */
	public URLValue getValue()
	{
		return new URLValue(this.key, this.url, this.updatingPeriod);
	}

	/*
	 * Expose the key of the URL. 11/23/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the URL. 11/23/2014, Bing Li
	 */
	public String getURL()
	{
		return this.url;
	}

	/*
	 * Expose the passed time. 11/23/2014, Bing Li
	 */
	public long getPassedTime()
	{
		return this.passedTime;
	}

	/*
	 * Clear the passed time. 11/23/2014, Bing Li
	 */
	public void clearPassedTime()
	{
		this.passedTime = 0;
	}

	/*
	 * Increment the passed time. The larger the passed time, the slower the server the URL resides. 11/23/2014, Bing Li
	 */
	public void incrementTime()
	{
		this.passedTime += CrawlConfig.CRAWLING_TIMER_PERIOD;
	}

	/*
	 * Expose the updating period. 11/23/2014, Bing Li
	 */
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}

	/*
	 * Increment the updating period. Since the crawling is performed periodically, the period is determined in a heuristic way. It is not the actual value. 11/23/2014, Bing Li
	 */
	public void incrementPeriod()
	{
		this.updatingPeriod += CrawlConfig.UPDATING_VALUE;
	}

	/*
	 * Decrement the updating period. 11/23/2014, Bing Li
	 */
	public void decrementPeriod()
	{
		// Check whether the updating period is larger than the updating value. 11/23/2014, Bing Li
		if (this.updatingPeriod > CrawlConfig.UPDATING_VALUE)
		{
			// The current updating period is reduced by the updating value if the updating period is larger than it. 11/23/2014, Bing Li
			this.updatingPeriod -= CrawlConfig.UPDATING_VALUE;
		}
		// Check whether the updating period is lower than the minimum updating period. 11/23/2014, Bing Li
		if (this.updatingPeriod < CrawlConfig.MIN_UPDATING_PERIOD)
		{
			// If the current updating period is too low, it is not necessary to cut it. Just set it to the minimum one. 11/23/2014, Bing Li
			this.updatingPeriod = CrawlConfig.MIN_UPDATING_PERIOD;
		}
	}

	/*
	 * Expose the last modified time. 11/23/2014, Bing Li
	 */
	public Date getLastModified()
	{
		return this.lastModified;
	}

	/*
	 * Update the last modified time. 11/23/2014, Bing Li
	 */
	public void updateLastModified(Date lastModified)
	{
		this.lastModified = lastModified;
	}
}
