package edu.chainnet.crawler.child.crawl;

import java.util.Date;
import java.util.Set;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 04/24/2021, Bing Li
@Entity
class HubSourceEntity
{
	@PrimaryKey
	private String hubKey;
	
	private String hubURL;
	private String hubTitle;
	private long passedTime;
	private long updatingPeriod;
	private Date lastModified;
	private Set<String> linkKeys;
	private String hash;
	
	public HubSourceEntity()
	{
	}
	
	public HubSourceEntity(String hubKey, String hubURL, String hubTitle, long passedTime, long updatingPeriod,
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

	public void setHubKey(String hubKey)
	{
		this.hubKey = hubKey;
	}

	public String getHubKey()
	{
		return this.hubKey;
	}
	
	public void setHubURL(String hubURL)
	{
		this.hubURL = hubURL;
	}
	
	public String getHubURL()
	{
		return this.hubURL;
	}

	public void setHubTitle(String hubTitle)
	{
		this.hubTitle = hubTitle;
	}

	public String getHubTitle()
	{
		return this.hubTitle;
	}
	
	public void setPassedTime(long passedTime)
	{
		this.passedTime = passedTime;
	}

	public long getPassedTime()
	{
		return this.passedTime;
	}
	
	public void setUpdatingPeriod(long up)
	{
		this.updatingPeriod = up;
	}
	
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
	
	public void setLastModified(Date ld)
	{
		this.lastModified = ld;
	}
	
	public Date getLastModified()
	{
		return this.lastModified;
	}
	
	public void setLinkKeys(Set<String> linkKeys)
	{
		this.linkKeys = linkKeys;
	}
	
	public Set<String> getLinkKeys()
	{
		return this.linkKeys;
	}

	public void setHash(String hash)
	{
		this.hash = hash;
	}
	
	public String getHash()
	{
		return this.hash;
	}

}
