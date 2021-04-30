package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.util.TimerTask;

import edu.chainnet.crawler.CrawlConfig;

// Created: 04/24/2021, Bing Li
class UploadAuthoritiesRunner extends TimerTask
{
	@Override
	public void run()
	{
		try
		{
			PagesUploader.CRAWL().upload(CrawlConfig.UPLOAD_AUTHORITIES_COUNT);
		}
		catch (IOException | InterruptedException | IllegalStateException e)
		{
			CrawlPrompts.print(e);
		}
	}
}

