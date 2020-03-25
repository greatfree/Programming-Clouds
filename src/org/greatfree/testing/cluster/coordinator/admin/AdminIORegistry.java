package org.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of AdminIOs. This is a singleton wrapper of ServerIORegistry. 11/24/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class AdminIORegistry
{
	// Declare an instance of ServerIORegistry for AdminIOs. 11/24/2014, Bing Li
	private ServerIORegistry<AdminIO> registry;
	
	/*
	 * Initializing ... 11/24/2014, Bing Li
	 */
	private AdminIORegistry()
	{
	}
	
	private static AdminIORegistry instance = new AdminIORegistry();
	
	public static AdminIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new AdminIORegistry();
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
		this.registry = new ServerIORegistry<AdminIO>();
	}

	/*
	 * Add a new instance of AdminIO to the registry. 11/24/2014, Bing Li
	 */
	public void addIO(AdminIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding AdminIOs. 11/24/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered AdminIOs. 11/24/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister an AdminIO. It is executed when a client is down or the connection gets something wrong. 11/24/2014, Bing Li
	 */
	public void removeIO(AdminIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered AdminIOs. It is executed when the server process is shutdown. 11/24/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
