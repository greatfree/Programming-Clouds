package org.greatfree.testing.crawlserver;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of CrawlServerIOs. This is a singleton wrapper of ServerIORegistry. 11/11/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public class CrawlIORegistry
{
	// Declare an instance of ServerIORegistry for CrawlServerIOs. 11/11/2014, Bing Li
	private ServerIORegistry<CrawlServerIO> registry;
	
	/*
	 * Initializing ... 11/11/2014, Bing Li
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
	 * Dispose the registry. 11/11/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/11/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<CrawlServerIO>();
	}

	/*
	 * Add a new instance of CrawlServerIO to the registry. 11/11/2014, Bing Li
	 */
	public void addIO(CrawlServerIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding CrawlServerIOs. 11/11/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered CrawlServerIOs. 11/11/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister a CrawlServerIO. It is executed when a client is down or the connection gets something wrong. 11/11/2014, Bing Li
	 */
	public void removeIO(CrawlServerIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered CrawlServerIOs. It is executed when the server process is shutdown. 11/11/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
