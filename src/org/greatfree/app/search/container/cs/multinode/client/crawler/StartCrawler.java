package org.greatfree.app.search.container.cs.multinode.client.crawler;

import java.io.IOException;

import org.greatfree.app.search.container.cluster.message.CrawledPagesNotification;
import org.greatfree.app.search.multicast.message.Page;
import org.greatfree.cluster.StandaloneClusterClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.Tools;

// Created: 01/14/2019, Bing Li
class StartCrawler
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		System.out.println("Crawler starting up ...");
		StandaloneClusterClient.CONTAINER().init(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, Tools.getHash(MulticastConfig.CLUSTER_SERVER_ROOT_NAME));
		System.out.println("Crawler started ...");
		
		Page page;
		int n = 0;
		for (int i = 0; i < 50; i++)
		{
			n = i % 2;
			if (n == 0)
			{
				page = new Page("Java Programming" + i, "Programming large-scale distributed systems ...", "http://www.cpu" + i + ".com/page" + i, true);
			}
			else
			{
				page = new Page("C++ Programming" + i, "Programming embedded systems ...", "http://www.memory" + i + ".com/page" + i, false);
			}
			StandaloneClusterClient.CONTAINER().asyncNotifyRoot(new CrawledPagesNotification(page));
			Thread.sleep(2000);
		}
		
		StandaloneClusterClient.CONTAINER().dispose();
	}

}
