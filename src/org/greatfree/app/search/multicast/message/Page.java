package org.greatfree.app.search.multicast.message;

import java.io.Serializable;

import org.greatfree.util.Tools;

// Created: 09/28/2018, Bing Li
public class Page implements Serializable
{
	private static final long serialVersionUID = 8664576142128708296L;
	
	private String key;
	
	private String title;
	private String text;
	private String url;
	private boolean isInternational;

	public Page(String title, String text, String url, boolean isInternational)
	{
		this.key = Tools.getHash(url);
		this.title = title;
		this.text = text;
		this.url = url;
		this.isInternational = isInternational;
	}
	
	public String getKey()
	{
		return this.key;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public String getURL()
	{
		return this.url;
	}
	
	public boolean isInternational()
	{
		return this.isInternational;
	}
}
