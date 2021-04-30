package edu.chainnet.crawler.coordinator;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.coordinator.manage.Paths;

// Created: 04/22/2021, Bing Li
class CrawlCoordinator
{
	private ClusterServerContainer server;

	private CrawlCoordinator()
	{
	}
	
	private static CrawlCoordinator instance = new CrawlCoordinator();
	
	public static CrawlCoordinator CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlCoordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
	}

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		Paths.CRAWL().dispose();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		Paths.CRAWL().init(CrawlConfig.CONFIG_PATH);
		this.server = new ClusterServerContainer(port, CrawlConfig.CRAWLING_COORDINATOR, task);
		this.server.start();
	}

}
