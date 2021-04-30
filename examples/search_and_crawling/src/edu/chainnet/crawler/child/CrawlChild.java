package edu.chainnet.crawler.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.child.crawl.ChildProfile;
import edu.chainnet.crawler.child.crawl.CrawlCacheConfig;
import edu.chainnet.crawler.child.crawl.CrawlScheduler;
import edu.chainnet.crawler.child.crawl.CrawlStates;
import edu.chainnet.crawler.child.crawl.HubRegistry;
import edu.chainnet.crawler.child.crawl.PersistedRawPagesQueue;
import edu.chainnet.crawler.child.crawl.SolrAuthorityPersister;
import edu.chainnet.crawler.coordinator.manage.CrawlChildPath;
import edu.chainnet.crawler.message.CrawlPathsRequest;
import edu.chainnet.crawler.message.CrawlPathsResponse;

// Created: 04/24/2021, Bing Li
class CrawlChild
{
	private ClusterChildContainer child;

	private CrawlChild()
	{
	}
	
	private static CrawlChild instance = new CrawlChild();
	
	public static CrawlChild CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		CrawlStates.CRAWL().disable();
		PersistedRawPagesQueue.WWW().dispose();
		HubRegistry.WWW().dispose();
		SolrAuthorityPersister.CRAWL().dispose();
		CrawlScheduler.CRAWL().dispose();

		TerminateSignal.SIGNAL().setTerminated();
//		StandaloneClient.CS().dispose();
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(CrawlConfig.CRAWLING_COORDINATOR_KEY);
		
		CrawlChildPath path = ((CrawlPathsResponse)this.child.readRoot(new CrawlPathsRequest(CrawlCacheConfig.CACHE_HOME, CrawlConfig.DOCS_HOME))).getPath();
		ChildProfile.CRAWL().setCachePath(path.getCachePath());
		ChildProfile.CRAWL().setDocPath(path.getDocPath());
		
		HubRegistry.WWW().init();
		PersistedRawPagesQueue.WWW().init(ChildProfile.CRAWL().getCachePath(), CrawlCacheConfig.AUTHORITY_SOLR_QUEUE_CACHE_KEY, CrawlCacheConfig.AUTHORITY_SOLR_QUEUE_CACHE_SIZE, CrawlCacheConfig.AUTHORITY_SOLR_QUEUE_CACHE_OFFHEAP_SIZE_IN_MB, CrawlCacheConfig.AUTHORITY_SOLR_QUEUE_CACHE_DISK_SIZE_IN_MB);

		SolrAuthorityPersister.CRAWL().init();
		CrawlScheduler.CRAWL().init();
	}
}

