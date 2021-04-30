package edu.chainnet.center;

import java.io.Serializable;
import java.util.Date;

// Created: 04/28/2021, Bing Li
public class PageIndex implements Serializable
{
	private static final long serialVersionUID = 279306152862545464L;
	
	private String pageKey;
	private String pageTitle;
	private String url;
	private String hubURL;
	private Date time;
	
	public PageIndex(String pageKey, String pageTitle, String url, String hubURL, Date time)
	{
		this.pageKey = pageKey;
		this.pageTitle = pageTitle;
		this.url = url;
		this.hubURL = hubURL;
		this.time = time;
	}

	public String getPageKey()
	{
		return this.pageKey;
	}
	
	public String getPageTitle()
	{
		return this.pageTitle;
	}
	
	public String getURL()
	{
		return this.url;
	}
	
	public String getHubURL()
	{
		return this.hubURL;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
