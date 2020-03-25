package org.greatfree.testing.cluster.coordinator.client;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of ClientIOs. This is a singleton wrapper of ServerIORegistry. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class ClientIORegistry
{
	// Declare an instance of ServerIORegistry for ClientIOs. 11/24/2014, Bing Li
	private ServerIORegistry<ClientIO> registry;
	
	/*
	 * Initializing ... 11/24/2014, Bing Li
	 */
	private ClientIORegistry()
	{
	}
	
	private static ClientIORegistry instance = new ClientIORegistry();
	
	public static ClientIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new ClientIORegistry();
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
		this.registry = new ServerIORegistry<ClientIO>();
	}

	/*
	 * Add a new instance of ClientIO to the registry. 11/24/2014, Bing Li
	 */
	public void addIO(ClientIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding ClientIOs. 11/24/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered ClientIOs. 11/24/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister a ClientIO. It is executed when a client is down or the connection gets something wrong. 11/24/2014, Bing Li
	 */
	public void removeIO(ClientIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered ClientIOs. It is executed when the server process is shutdown. 11/24/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
