package org.greatfree.testing.coordinator.searching;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of SearchIOs. This is a singleton wrapper of ServerIORegistry. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchIORegistry
{
	// Declare an instance of ServerIORegistry for SearchIOs. 11/29/2014, Bing Li
	private ServerIORegistry<SearchIO> registry;
	
	/*
	 * Initializing ... 11/29/2014, Bing Li
	 */
	private SearchIORegistry()
	{
	}
	
	private static SearchIORegistry instance = new SearchIORegistry();
	
	public static SearchIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new SearchIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/29/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/29/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<SearchIO>();
	}

	/*
	 * Add a new instance of SearchIO to the registry. 11/29/2014, Bing Li
	 */
	public void addIO(SearchIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding SearchIOs. 11/29/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered SearchIOs. 11/29/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister an SearchIO. It is executed when a client is down or the connection gets something wrong. 11/29/2014, Bing Li
	 */
	public void removeIO(SearchIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered SearchIOs. It is executed when the server process is shutdown. 11/29/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
