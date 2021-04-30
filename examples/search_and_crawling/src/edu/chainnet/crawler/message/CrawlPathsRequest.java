package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.container.ChildRootRequest;

// Created: 04/26/2021, Bing Li
public class CrawlPathsRequest extends ChildRootRequest
{
	private static final long serialVersionUID = -5700958457263798723L;

	private String cachePath;
	private String docPath;

	public CrawlPathsRequest(String cachePath, String docPath)
	{
		super(CrawlingApplicationID.CRAWL_PATHS_REQUEST);
		this.cachePath = cachePath;
		this.docPath = docPath;
	}
	
	public String getCachePath()
	{
		return this.cachePath;
	}

	public String getDocPath()
	{
		return this.docPath;
	}
}
