package edu.chainnet.crawler;

import org.greatfree.util.UtilConfig;

import java.io.Serializable;

// Created: 04/24/2021, Bing Li
public class Hub implements Serializable
{
	private static final long serialVersionUID = 911734672177511121L;

	private String hubKey;

	private String hubURL;
	private String hubTitle;
	private long updatingPeriod;
	
	public Hub()
	{
		this.hubKey = CrawlConfig.NO_HUB_KEY;
		this.hubURL = CrawlConfig.NO_HUB_URL;
		this.hubTitle = UtilConfig.EMPTY_STRING;
		this.updatingPeriod = CrawlConfig.NO_UPDATING_PERIOD;
	}
	
	public Hub(String hubKey, String hubURL, String hubTitle, long updatingPeriod)
	{
		this.hubKey = hubKey;
		this.hubURL = hubURL;
		this.hubTitle = hubTitle;
		this.updatingPeriod = updatingPeriod;
	}
	
	public String getHubKey()
	{
		return this.hubKey;
	}
	
	public String getHubURL()
	{
		return this.hubURL;
	}

	public String getHubTitle()
	{
		return this.hubTitle;
	}

	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
	
	public String toString()
	{
		return this.hubTitle + UtilConfig.COLON + this.hubURL;
	}
}
