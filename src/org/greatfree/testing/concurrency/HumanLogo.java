package org.greatfree.testing.concurrency;

import java.io.Serializable;

// Created: 04/23/2018, Bing Li
class HumanLogo implements Serializable
{
	private static final long serialVersionUID = 5403250496594086405L;

	private String humanKey;
	private String hubURL;
	private String logoURL;
	private boolean isSucceeded;

	public HumanLogo(String humanKey, String hubURL, String logoURL, boolean isSucceeded)
	{
		this.humanKey = humanKey;
		this.hubURL = hubURL;
		this.logoURL = logoURL;
		this.isSucceeded = isSucceeded;
	}
	
	public String getHumanKey()
	{
		return this.humanKey;
	}
	
	public String getHubURL()
	{
		return this.hubURL;
	}
	
	public String getLogoURL()
	{
		return this.logoURL;
	}
	
	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
