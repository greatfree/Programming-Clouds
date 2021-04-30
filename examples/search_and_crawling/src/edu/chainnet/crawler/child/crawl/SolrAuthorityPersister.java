package edu.chainnet.crawler.child.crawl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationDispatcher;

import edu.chainnet.crawler.AuthoritySolrValue;
import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.message.CrawledAuthoritiesNotification;

// Created: 04/24/2021, Bing Li
public class SolrAuthorityPersister
{
	private NotificationDispatcher<CrawledAuthoritiesNotification, CacheAuthorityThread, CacheAuthorityThreadCreator> pageSaver;
	private ThreadPool pool;
	private AtomicBoolean isDown;
	
	private SolrAuthorityPersister()
	{
		this.isDown = new AtomicBoolean(false);
	}
	
	private static SolrAuthorityPersister instance = new SolrAuthorityPersister();
	
	public static SolrAuthorityPersister CRAWL()
	{
		if (instance == null)
		{
			instance = new SolrAuthorityPersister();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException
	{
//		System.out.println("Disposing SolrAuthorityTransfer ...");
		this.isDown.set(true);
		
		this.pageSaver.dispose();
		this.pool.shutdown(CrawlConfig.SOLR_AUTHORITY_PERSISTER_THREAD_POOL_SHUTDOWN_TIMEOUT);
//		System.out.println("SolrAuthorityTransfer disposed ...");
	}
	
	public void init()
	{
		this.pool = new ThreadPool(CrawlConfig.SOLR_AUTHORITY_PERSISTER_THREAD_POOL_SIZE, CrawlConfig.SOLR_AUTHORITY_PERSISTER_THREAD_KEEP_ALIVE_TIME);
		
		this.pageSaver = new NotificationDispatcher.NotificationDispatcherBuilder<CrawledAuthoritiesNotification, CacheAuthorityThread, CacheAuthorityThreadCreator>()
				.threadCreator(new CacheAuthorityThreadCreator())
				.poolSize(CrawlConfig.MAX_SOLR_AUTHORITY_PERSISTENT_POOL_SIZE)
				.notificationQueueSize(CrawlConfig.MAX_SOLR_AUTHORITY_PERSISTENT_TASK_SIZE)
				.dispatcherWaitTime(CrawlConfig.SOLR_AUTHORITY_PERSISTENT_DISPATCHER_WAIT_TIME)
				.waitRound(CrawlConfig.SOLR_AUTHORITY_PERSISTENT_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(CrawlConfig.SOLR_AUTHORITY_PERSISTENT_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(CrawlConfig.SOLR_AUTHORITY_PERSISTENT_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.build();
	}
	
	public void send(List<AuthoritySolrValue> authorities)
	{
		if (!this.isDown.get())
		{
			if (!this.pageSaver.isReady())
			{
				this.pool.execute(this.pageSaver);
			}
			this.pageSaver.enqueue(new CrawledAuthoritiesNotification(authorities));
		}
	}
}
