package com.greatfree.testing.server;

import java.io.IOException;
import java.util.Set;

import com.greatfree.remote.ServerIORegistry;

/*
 * The class keeps all of MyServerIOs. This is a singleton wrapper of ServerIORegistry. 08/22/2014, Bing Li
 */

// Created: 08/22/2014, Bing Li
public class MyServerIORegistry
{
	// Declare an instance of ServerIORegistry for MyServerIOs. 08/22/2014, Bing Li
	private ServerIORegistry<MyServerIO> registry;
	
	/*
	 * Initializing ... 08/22/2014, Bing Li
	 */
	private MyServerIORegistry()
	{
	}
	
	private static MyServerIORegistry instance = new MyServerIORegistry();
	
	public static MyServerIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new MyServerIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 08/22/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/07/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<MyServerIO>();
	}

	/*
	 * Add a new instance of MyServerIO to the registry. 08/22/2014, Bing Li
	 */
	public void addIO(MyServerIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding MyServerIOs. 08/22/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered MyServerIOs. 08/22/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister an MyServerIO. It is executed when a client is down or the connection gets something wrong. 08/10/2014, Bing Li
	 */
	public void removeIO(MyServerIO io) throws IOException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered MyServerIOs. It is executed when the server process is shutdown. 08/10/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException
	{
		this.registry.removeAllIOs();
	}
}
