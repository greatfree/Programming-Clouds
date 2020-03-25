package org.greatfree.testing.coordinator.crawling;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of CrawlIOs. This is a singleton wrapper of ServerIORegistry. 11/24/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CrawlIORegistry
{
	// Declare an instance of ServerIORegistry for CrawlIOs. 11/24/2014, Bing Li
	private ServerIORegistry<CrawlIO> registry;
	
	/*
	 * Initializing ... 11/24/2014, Bing Li
	 */
	private CrawlIORegistry()
	{
	}
	
	private static CrawlIORegistry instance = new CrawlIORegistry();
	
	public static CrawlIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new CrawlIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/24/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/24/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<CrawlIO>();
	}

	/*
	 * Add a new instance of CrawlIO to the registry. 11/24/2014, Bing Li
	 */
	public void addIO(CrawlIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding CrawlIOs. 11/24/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered CrawlIOs. 11/24/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister an CrawlIO. It is executed when a client is down or the connection gets something wrong. 11/24/2014, Bing Li
	 */
	public void removeIO(CrawlIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered CrawlIOs. It is executed when the server process is shutdown. 11/24/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
