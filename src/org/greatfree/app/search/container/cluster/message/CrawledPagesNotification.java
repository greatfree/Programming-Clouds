package org.greatfree.app.search.container.cluster.message;

import org.greatfree.app.search.multicast.message.Page;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/14/2019, Bing Li
public class CrawledPagesNotification extends ClusterNotification
{
	private static final long serialVersionUID = -4423624200126745443L;

	private Page page;

	public CrawledPagesNotification(Page page)
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, SearchApplicationID.CRAWLED_PAGES_NOTIFICATION);
		this.page = page;
	}

	public Page getPage()
	{
		return this.page;
	}
}
