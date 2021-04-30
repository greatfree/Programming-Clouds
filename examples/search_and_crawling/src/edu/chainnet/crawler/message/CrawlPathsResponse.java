package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.container.ChildRootResponse;

import edu.chainnet.crawler.coordinator.manage.CrawlChildPath;

// Created: 04/26/2021, Bing Li
public class CrawlPathsResponse extends ChildRootResponse
{
	private static final long serialVersionUID = 3405954289816934383L;

	private CrawlChildPath path;

	public CrawlPathsResponse(CrawlChildPath path)
	{
		super(CrawlingApplicationID.CRAWL_PATHS_RESPONSE);
		this.path = path;
	}

	public CrawlChildPath getPath()
	{
		return this.path;
	}
}

