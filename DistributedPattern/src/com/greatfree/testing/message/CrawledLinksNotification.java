package com.greatfree.testing.message;

import java.util.Set;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.testing.data.CrawledLink;

/*
 * The notification contains the crawled links to be sent to the coordinator from the crawler. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawledLinksNotification extends ServerMessage
{
	private static final long serialVersionUID = -2613694876747449900L;

	// The crawled links. 11/23/2014, Bing Li
	private Set<CrawledLink> links;

	/*
	 * Initialize the crawled links. 11/23/2014, Bing Li
	 */
	public CrawledLinksNotification(Set<CrawledLink> links)
	{
		super(MessageType.CRAWLED_LINKS_NOTIFICATION);
		this.links = links;
	}

	/*
	 * Expose the crawled links. 11/23/2014, Bing Li
	 */
	public Set<CrawledLink> getLinks()
	{
		return this.links;
	}
}
