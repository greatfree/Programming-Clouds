package org.greatfree.client;

import org.greatfree.util.FreeObject;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/*
 * The class consists of all of the values to create an instance of FreeClient. For example, it is the source that is used in RetrievablePool. 09/17/2014, Bing Li
 */

// Created: 09/17/2014, Bing Li
public class IPResource extends FreeObject
{
	// The IP address. 09/17/2014, Bing Li
	private String ip;
	// The port number. 09/17/2014, Bing Li
	private int port;
	// The time is set to avoid permanent hungs when reading remotely. 03/25/2020, Bing Li
	private int timeout;

	/*
	 * Initialize the instance of the class. 09/17/2014, Bing Li
	 */
	public IPResource(String ip, int port, int timeout)
	{
		// Create and set the key of the class. The key represents all of the FreeClients that connect to the same remote end. 09/17/2014, Bing Li
		super(Tools.getKeyOfFreeClient(ip, port));
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}

	public IPResource(String ip, int port)
	{
		// Create and set the key of the class. The key represents all of the FreeClients that connect to the same remote end. 09/17/2014, Bing Li
		super(Tools.getKeyOfFreeClient(ip, port));
		this.ip = ip;
		this.port = port;
		this.timeout = 0;
	}

	/*
	 * Initialize the instance of the class. 09/17/2014, Bing Li
	 */
	public IPResource(IPAddress ip)
	{
		// Create and set the key of the class. The key represents all of the FreeClients that connect to the same remote end. 09/17/2014, Bing Li
		super(Tools.getKeyOfFreeClient(ip.getIP(), ip.getPort()));
		this.ip = ip.getIP();
		this.port = ip.getPort();
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
	
	public int getTimeout()
	{
		return this.timeout;
	}
	
	public IPAddress getAddress()
	{
//		return new IPAddress(this.getObjectKey(), this.ip, this.port);
		return new IPAddress(this.ip, this.port);
	}
}
