package com.greatfree.remote;

import com.greatfree.util.FreeObject;
import com.greatfree.util.Tools;

/*
 * The class consists of all of the values to create an instance of FreeClient. For example, it is the source that is used in RetrievablePool. 09/17/2014, Bing Li
 */

// Created: 09/17/2014, Bing Li
public class IPPort extends FreeObject
{
	// The IP address. 09/17/2014, Bing Li
	private String ip;
	// The port number. 09/17/2014, Bing Li
	private int port;

	/*
	 * Initialize the instance of the class. 09/17/2014, Bing Li
	 */
	public IPPort(String ip, int port)
	{
		// Create and set the key of the class. The key represents all of the FreeClients that connect to the same remote end. 09/17/2014, Bing Li
		super(Tools.getKeyOfFreeClient(ip, port));
		this.ip = ip;
		this.port = port;
	}

	/*
	 * Expose the IP address. 09/17/2014, Bing Li
	 */
	public String getIP()
	{
		return this.ip;
	}

	/*
	 * Expose the port number. 09/17/2014, Bing Li
	 */
	public int getPort()
	{
		return this.port;
	}
}
