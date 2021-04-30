package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.IPAddress;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.crawler.AuthoritySolrValue;
import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.message.UploadPagesNotification;

// Created: 04/24/2021, Bing Li
class PagesUploader
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.child.crawl");

	private IPAddress dataCenterAddress;
	
	private PagesUploader()
	{
	}
	
	private static PagesUploader instance = new PagesUploader();
	
	public static PagesUploader CRAWL()
	{
		if (instance == null)
		{
			instance = new PagesUploader();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException, IOException
	{
		StandaloneClient.CS().dispose();
	}
	
	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		this.dataCenterAddress = StandaloneClient.CS().getIPAddress(CrawlConfig.REGISTRY_IP, RegistryConfig.PEER_REGISTRY_PORT, CenterConfig.CENTER_COORDINATOR_KEY);
	}
	
	public void upload(int count) throws IOException, InterruptedException
	{
		while (!PersistedRawPagesQueue.WWW().isEmpty())
		{
			List<AuthoritySolrValue> authorities = PersistedRawPagesQueue.WWW().loadAuthorities(count);
			if (authorities.size() > 0)
			{
				log.info("Pages: " + authorities.size() + " are to be uploaded!");
				StandaloneClient.CS().syncNotify(this.dataCenterAddress.getIP(), this.dataCenterAddress.getPort(), new UploadPagesNotification(authorities));
			}
		}
	}

}
