package edu.chainnet.center.child.lucene;

import java.io.Serializable;
import java.util.Date;

// Created: 04/27/2021, Bing Li
public class Page implements Serializable
{
	private static final long serialVersionUID = -6300596571817165288L;
	
	private String pageKey;
	private String url;
	private String title;
	private String content;
	private Date time;
	private String hostHub;
	
	public Page(String pk, String url, String title, String content, Date time, String hostHub)
	{
		this.pageKey = pk;
		this.url = url;
		this.title = title;
		this.content = content;
		this.time = time;
		this.hostHub = hostHub;
	}
	
	public String getPageKey()
	{
		return this.pageKey;
	}
	
	public String getURL()
	{
		return this.url;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getContent()
	{
		return this.content;
	}
	
	public Date getTime()
	{
		return this.time;
	}
	
	public String getHostHub()
	{
		return this.hostHub;
	}
}

