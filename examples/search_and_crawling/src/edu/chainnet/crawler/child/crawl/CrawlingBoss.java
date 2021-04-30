package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.util.Timer;

import org.greatfree.concurrency.Threader;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NullObject;

import edu.chainnet.crawler.CrawlConfig;

// Created: 04/24/2021, Bing Li
public class CrawlingBoss
{
	private Threader<HubCrawlConsumer> consumer;
	private CrawlerStateChecker stateChecker;
	private Timer crawlCheckingTimer;

	private Timer uploadAuthoritiesTimer;
	private UploadAuthoritiesRunner uploadRunner;

	private CrawlingBoss()
	{
		
	}

	private static CrawlingBoss instance = new CrawlingBoss();
	
	public static CrawlingBoss CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlingBoss();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException, IOException
	{
		PagesUploader.CRAWL().dispose();
		CrawlStates.CRAWL().dispose();
		this.consumer.stop(CrawlConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);
		if (this.stateChecker != null)
		{
			this.stateChecker.cancel();
			this.crawlCheckingTimer.cancel();
		}
		this.uploadRunner.cancel();
		this.uploadAuthoritiesTimer.cancel();
	}
	
	public void stopCrawl()
	{
		CrawlStates.CRAWL().disable();
	}

	public void startCrawl() throws ClassNotFoundException, RemoteReadException, IOException
	{
		CrawlStates.CRAWL().init();
		PagesUploader.CRAWL().init();
		if (this.consumer == null)
		{
			this.consumer = new Threader<HubCrawlConsumer>(new HubCrawlConsumer(new CrawlEater(), CrawlConfig.HUB_CRAWL_SCHEDULER_WAIT_TIME));
			this.consumer.start();
			this.consumer.getFunction().produce(new NullObject());
		}

		if (this.stateChecker == null)
		{
			this.stateChecker = new CrawlerStateChecker();
			this.crawlCheckingTimer = new Timer();
			this.crawlCheckingTimer.schedule(this.stateChecker, 0, CrawlConfig.CRAWLER_STATE_CHECK_PERIOD);
		}

		if (this.uploadRunner == null)
		{
			this.uploadRunner = new UploadAuthoritiesRunner();
			this.uploadAuthoritiesTimer = new Timer();
			this.uploadAuthoritiesTimer.schedule(this.uploadRunner, 0, CrawlConfig.UPLOAD_AUTHORITIES_CHECK_PERIOD);
		}
		
		CrawlStates.CRAWL().enable();
	}
}
