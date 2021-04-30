package edu.chainnet.crawler;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.greatfree.util.Time;

// Created: 04/24/2021, Bing Li
public class HubSource implements Serializable
{
	private static final long serialVersionUID = -4534445611163448570L;
	
	private String hubKey;
	private String hubURL;
	private String hubTitle;
	private long passedTime;
	private long updatingPeriod;
	private Date lastModified;
	private Set<String> linkKeys;
	private String hash;

	public HubSource(String hubKey, String hubURL, String hubTitle, long passedTime, long updatingPeriod,
					 Date lastModified,
					 Set<String> linkKeys, String hash)
	{
		this.hubKey = hubKey;
		this.hubURL = hubURL;
		this.hubTitle = hubTitle;
		this.passedTime = passedTime;
		this.updatingPeriod = updatingPeriod;
		this.lastModified = lastModified;
		this.linkKeys = linkKeys;
		this.hash = hash;
	}
	
	public HubSource(Hub hv)
	{
		this.hubKey = hv.getHubKey();
		this.hubURL = hv.getHubURL();
		this.hubTitle = hv.getHubTitle();
		this.passedTime = 0;
		this.updatingPeriod = hv.getUpdatingPeriod();
		this.lastModified = Time.INIT_TIME;
		this.linkKeys = CrawlConfig.NO_LINK_KEYS;
		this.hash = CrawlConfig.NO_HASH;
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

	public long getPassedTime()
	{
		return this.passedTime;
	}
	
	public void setPassedTime(long passedTime)
	{
		this.passedTime = passedTime;
	}
	
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
	
	public void setUpdatingPeriod(long up)
	{
		this.updatingPeriod = up;
	}
	
	public Date getLastModified()
	{
		return this.lastModified;
	}
	
	public void setLastModified(Date ld)
	{
		this.lastModified = ld;
	}
	
	public Set<String> getLinkKeys()
	{
		return this.linkKeys;
	}
	
	public void setLinkKeys(Set<String> linkKeys)
	{
		this.linkKeys = linkKeys;
	}
	
	public String getHash()
	{
		return this.hash;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
	}
}
