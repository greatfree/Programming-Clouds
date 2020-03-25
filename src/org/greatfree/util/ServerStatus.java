package org.greatfree.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * When a distributed node, a server/a client, is shutdown, some exceptions are raised. But in most times, it is not necessary to show them because they are less important than the ones at runtime.
 * 
 * The class attempts to filter those exceptions that are caught when a node is shutdown. Only runtime exceptions are raised.
 * 
 * 01/30/2016, bing Li
 */

// Created: 01/30/2016, Bing Li
public class ServerStatus
{
	// The collection that keeps the status of servers. It assumes that there are multiple nodes in the system. If one of them is shutdown, it affects others and it is not necessary to show the exceptions once if it happens. 01/30/2016, Bing Li
	private Map<String, Boolean> serverStatus;

	/*
	 * The singleton definition. 01/30/2016, Bing Li
	 */
	private ServerStatus()
	{
		this.serverStatus = new ConcurrentHashMap<String, Boolean>();
	}
	
	private static ServerStatus instance = new ServerStatus();
	
	public static ServerStatus FREE()
	{
		if (instance == null)
		{
			instance = new ServerStatus();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Initialize the class. The parameters are the server IDs in the system. The method is invoked when multiple servers are associated. 01/30/2016, Bing Li
	 */
	public void addServerIDs(List<String> serverIDs)
	{
		// Initialize the collection that keeps the server status. 01/30/2016, Bing Li
//		this.serverStatus = new ConcurrentHashMap<String, Boolean>();
		for (String id : serverIDs)
		{
			this.serverStatus.put(id, false);
		}
	}
	
	/*
	 * Initialize the class. The parameter is the server ID in the system. The method is invoked when the server is standalone. 01/30/2016, Bing Li
	 */
	public void init(String serverID)
	{
		// Initialize the collection that keeps the server status. 01/30/2016, Bing Li
//		this.serverStatus = new ConcurrentHashMap<String, Boolean>();
		this.serverStatus.put(serverID, false);
	}
	
	public void init()
	{
		this.serverStatus.put(Tools.generateUniqueKey(), false);
	}

	/*
	 * Add one server ID into the status. 04/18/2017, Bing Li
	 */
	public void addServerID(String serverID)
	{
		/*
		if (this.serverStatus == null)
		{
			this.serverStatus = new ConcurrentHashMap<String, Boolean>();
		}
		*/
		if (!this.serverStatus.containsKey(serverID))
		{
			this.serverStatus.put(serverID, false);
		}
	}

	/*
	 * Set one server as shutdown. 01/30/2016, Bing Li
	 */
	public void setShutdown(String id)
	{
		if (this.serverStatus.containsKey(id))
		{
			this.serverStatus.put(id, true);
		}
	}
	
	/*
	 * Set all server as shutdown. 01/30/2016, Bing Li
	 */
	public void setShutdown()
	{
		Set<String> keys = this.serverStatus.keySet();
		for (String key : keys)
		{
			this.serverStatus.put(key, true);
		}
	}

	/*
	 * Check whether one server, whose ID is id, is shutdown or not. 02/06/2016, Bing Li
	 */
	public boolean isServerDown(String id)
	{
		return this.serverStatus.get(id);
	}

	/*
	 * Check whether any one of the servers is shutdown. 01/30/2016, Bing Li
	 */
	private boolean isAnyServerShutdown()
	{
		/*
		for (String serverID : this.serverStatus.keySet())
		{
			System.out.println("Existing server id = " + serverID);
		}
		*/
		for (Boolean entry : this.serverStatus.values())
		{
			if (entry)
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * Print exceptions when no any servers are shutdown. 01/30/2016, Bing Li
	 */
	public void printException(Exception e)
	{
		if (!this.isAnyServerShutdown())
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Print exceptions when no any servers are shutdown. 01/30/2016, Bing Li
	 */
	public void printException(String e)
	{
		if (!this.isAnyServerShutdown())
		{
			System.out.println(e);
		}
	}
}
