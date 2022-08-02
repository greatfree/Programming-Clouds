package org.greatfree.testing.client;

import java.io.IOException;
import java.util.Set;

import org.greatfree.server.ServerIORegistry;

/*
 * The registry keeps all of the connections' server IOs. 11/30/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerIORegistry
{
	// Declare an instance of ServerIORegistry for ClientServerIOs. 11/07/2014, Bing Li
	private ServerIORegistry<ClientServerIO> registry;
	
	/*
	 * Initializing ... 11/07/2014, Bing Li
	 */
	private ClientServerIORegistry()
	{
	}

	/*
	 * Define a singleton for the registry. 11/07/2014, Bing Li
	 */
	private static ClientServerIORegistry instance = new ClientServerIORegistry();
	
	public static ClientServerIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new ClientServerIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/07/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/07/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<ClientServerIO>();
	}

	/*
	 * Add a new instance of ClientServerIO to the registry. 11/07/2014, Bing Li
	 */
	public void addIO(ClientServerIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding ClientServerIOs. 11/07/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered ClientServerIOs. 11/07/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister a ClientServerIO. It is executed when a client is down or the connection gets something wrong. 11/07/204, Bing Li
	 */
	public void removeIO(ClientServerIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered ClientServerIOs. It is executed when the server process is shutdown. 11/07/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
