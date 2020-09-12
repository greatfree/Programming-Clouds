package org.greatfree.app.search.dip.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/28/2018, Bing Li
public class CrawledPagesNotification extends MulticastNotification
{
	private static final long serialVersionUID = 1993542179579631587L;

	private Page page;

	public CrawledPagesNotification(Page page)
	{
		super(SearchMessageType.CRAWLED_PAGES_NOTIFICATION);
		this.page = page;
	}

	public Page getPage()
	{
		return this.page;
	}
}
