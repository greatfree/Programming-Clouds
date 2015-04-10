package com.greatfree.testing.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * This is a collection to keep all of the keys of online clients. It is used to manage clients, such as multicasting. 11/09/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class ClientRegistry
{
	// Declare a list to keep all of the keys. 11/09/2014, Bing Li
	private List<String> clientKeys;

	/*
	 * Initialize. 11/09/2014, Bing Li
	 */
	private ClientRegistry()
	{
		// Define a synchronized list for the consideration of consistency. 11/09/2014, Bing Li
		this.clientKeys = new CopyOnWriteArrayList<String>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static ClientRegistry instance = new ClientRegistry();
	
	public static ClientRegistry MANAGEMENT()
	{
		if (instance == null)
		{
			instance = new ClientRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/09/2014, Bing Li
	 */
	public void dispose()
	{
		this.clientKeys.clear();
		this.clientKeys = null;
	}

	/*
	 * Register the client key. 11/09/2014, Bing Li
	 */
	public void register(String clientKey)
	{
		// Detect if the client key exists. 11/09/2014, Bing Li
		if (!this.clientKeys.contains(clientKey))
		{
			// Add the client key if it does not exist. 11/09/2014, Bing Li
			this.clientKeys.add(clientKey);
		}
	}

	/*
	 * Unregister the client key. 11/09/2014, Bing Li
	 */
	public void unregister(String clientKey)
	{
		// Check if the client key exists. 11/09/2014, Bing Li
		if (this.clientKeys.contains(clientKey))
		{
			// Remove the client key if it exists. 11/09/2014, Bing Li
			this.clientKeys.remove(clientKey);
		}
	}

	/*
	 * Return the count of all of the registered clients. 11/09/2014, Bing Li
	 */
	public int getClientCount()
	{
		return this.clientKeys.size();
	}

	/*
	 * Return all of the registered client keys. 11/09/2014, Bing Li
	 */
	public List<String> getClientKeys()
	{
		return this.clientKeys;
	}
}
