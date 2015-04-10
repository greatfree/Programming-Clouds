package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.testing.data.CrawledLink;

/*
 * The notification contains one crawled link. It is sent to one distributed memory node for storage. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class AddCrawledLinkNotification extends ServerMessage
{
	private static final long serialVersionUID = 2093100652281316873L;

	// The memory server key. 11/28/2014, Bing Li
	private String dcKey;
	// The crawled link. 11/28/2014, Bing Li
	private CrawledLink link;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public AddCrawledLinkNotification(String dcKey, CrawledLink link)
	{
		super(MessageType.ADD_CRAWLED_LINK_NOTIFICATION);
		this.dcKey = dcKey;
		this.link = link;
	}

	/*
	 * Expose the memory server key. 11/28/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}

	/*
	 * Expose the crawled link. 11/28/2014, Bing Li
	 */
	public CrawledLink getLink()
	{
		return this.link;
	}
}
