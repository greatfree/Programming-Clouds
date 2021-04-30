package edu.chainnet.crawler.client.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 04/22/2021, Bing Li
@Entity
class HubEntity
{
	@PrimaryKey
	private String hubKey;

	private String hubURL;
	private String hubTitle;
	private long updatingPeriod;
	private boolean isAssigned;

	public HubEntity()
	{
	}
	
	public HubEntity(String hubKey, String hubURL, String hubTitle, long updatingPeriod, boolean isAssigned)
	{
		this.hubKey = hubKey;
		this.hubURL = hubURL;
		this.hubTitle = hubTitle;
		this.updatingPeriod = updatingPeriod;
		this.isAssigned = isAssigned;
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

	public void setUpdatingPeriod(long updatingPeriod)
	{
		this.updatingPeriod = updatingPeriod;
	}
	
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
	
	public void setAssigned(boolean isAssigned)
	{
		this.isAssigned = isAssigned;
	}
	
	public boolean isAssigned()
	{
		return this.isAssigned;
	}
}

