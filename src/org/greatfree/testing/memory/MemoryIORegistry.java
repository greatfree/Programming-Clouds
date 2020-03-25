package org.greatfree.testing.memory;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.ServerIORegistry;

/*
 * The class keeps all of MemoryIOs. This is a singleton wrapper of ServerIORegistry. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryIORegistry
{
	// Declare an instance of ServerIORegistry for MemoryIOs. 11/27/2014, Bing Li
	private ServerIORegistry<MemoryIO> registry;
	
	/*
	 * Initializing ... 11/27/2014, Bing Li
	 */
	private MemoryIORegistry()
	{
	}
	
	private static MemoryIORegistry instance = new MemoryIORegistry();
	
	public static MemoryIORegistry REGISTRY()
	{
		if (instance == null)
		{
			instance = new MemoryIORegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/27/2014, Bing Li
	 */
	public void dispose() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}

	/*
	 * Initialize the registry. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.registry = new ServerIORegistry<MemoryIO>();
	}

	/*
	 * Add a new instance of MemoryIO to the registry. 11/27/2014, Bing Li
	 */
	public void addIO(MemoryIO io)
	{
		this.registry.addIO(io);
	}

	/*
	 * Get all of the IPs of the connected clients from the corresponding MemoryIOs. 11/27/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
		return this.registry.getIPs();
	}

	/*
	 * Get the count of the registered MemoryIOs. 11/27/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.registry.getIOCount();
	}

	/*
	 * Remove or unregister a MemoryIO. It is executed when a client is down or the connection gets something wrong. 11/27/2014, Bing Li
	 */
	public void removeIO(MemoryIO io) throws IOException, InterruptedException
	{
		this.registry.removeIO(io);
	}

	/*
	 * Remove or unregister all of the registered MemoryIOs. It is executed when the server process is shutdown. 11/27/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		this.registry.removeAllIOs();
	}
}
