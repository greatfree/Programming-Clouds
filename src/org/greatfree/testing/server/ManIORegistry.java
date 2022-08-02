package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Set;

import org.greatfree.server.ServerIORegistry;

/*
 * The class keeps all of ManIOs. This is a singleton wrapper of ServerIORegistry. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
class ManIORegistry
{
	// Declare an instance of ServerIORegistry for ManIOs. 01/20/2016, Bing Li
	private ServerIORegistry<ManIO> registry;

	/*
	 * Initializing ... 01/20/2016, Bing Li
	 */
	private ManIORegistry()
	{
	}
	
	private static ManIORegistry instance = new ManIORegistry();
	
	public static ManIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new ManIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the registry. 01/20/2016, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 01/20/2016, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<ManIO>();
	}

	/*
	 * Add a new instance of ManIO to the registry. 01/20/2016, Bing Li
	 */
	public void addIO(ManIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding ManIOs. 01/20/2016, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered ManIOs. 01/20/2016, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister an ManIO. It is executed when a client is down or the connection gets something wrong. 08/10/2014, Bing Li
	 */
	public void removeIO(ManIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered ManIOs. It is executed when the server process is shutdown. 08/10/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
