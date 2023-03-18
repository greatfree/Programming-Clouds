package org.greatfree.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.UtilConfig;

/*
 * The class is used to keep all of the ServerIOs, which are assigned to each client for the interactions between the server and the corresponding client. 08/04/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
public class ServerIORegistry<IO extends ServerIO>
{
	// A map to keep all connected ServerIOs. 08/04/2014, Bing Li
	private Map<String, IO> ioRegistry;

	/*
	 * The map is initialized as the one which has the ability of concurrency control. 08/04/2014, Bing Li
	 */
	public ServerIORegistry()
	{
		this.ioRegistry = new ConcurrentHashMap<String, IO>();
	}
	

	/*
	 * Add a new connected ServerIO. 08/04/2014, Bing Li
	 */
	public void addIO(IO io)
	{
		this.ioRegistry.put(io.getClientKey(), io);
	}

	/*
	 * Get the count of the current available ServerIOs. 08/04/2014, Bing Li
	 */
	public int getIOCount()
	{
		return this.ioRegistry.size();
	}

	/*
	 * Get all of the available IPs. 08/04/2014, Bing Li
	 */
	public Set<String> getIPs()
	{
//		Set<String> ips = Sets.newHashSet();
		Set<String> ips = new HashSet<String>();
		for (IO io : this.ioRegistry.values())
		{
			ips.add(io.getIP());
		}
		return ips;
	}

	/*
	 * Get the IP of a particular client. 08/04/2014, Bing Li
	 */
	public String getIP(String clientKey)
	{
		if (this.ioRegistry.containsKey(clientKey))
		{
			return this.ioRegistry.get(clientKey).getIP();
		}
		return UtilConfig.NO_IP;
	}

	/*
	 * Remove a particular client. 08/04/2014, Bing Li
	 */
	public void removeIO(IO io) throws IOException, InterruptedException
	{
		if (this.ioRegistry.containsKey(io.getClientKey()))
		{
			this.ioRegistry.remove(io.getClientKey());
		}
		io.shutdown();
	}

	/*
	 * Remove all of the ServerIOs. 08/04/2014, Bing Li
	 */
	public void removeAllIOs() throws IOException, InterruptedException
	{
		for (IO io : this.ioRegistry.values())
		{
			io.shutdown();
		}
		this.ioRegistry.clear();
	}
}
