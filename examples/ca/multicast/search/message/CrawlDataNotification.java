package ca.multicast.search.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/14/2020, Bing Li
public class CrawlDataNotification extends MulticastMessage
{
	private static final long serialVersionUID = -7599070520802709030L;
	
	private String text;

	public CrawlDataNotification(String text)
	{
		super(SearchConfig.CRAWL_DATA_NOTIFICATION);
		this.text = text;
	}

	public String getText()
	{
		return this.text;
	}
}
