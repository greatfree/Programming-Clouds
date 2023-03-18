package org.greatfree.util;

import java.io.Serializable;

import org.greatfree.exceptions.PeerNameIsNullException;

/*
 * The IP address information is enclosed in the class. It is usually transmitted from the registry server to a cluster root. So, it must implement the interface of Serializable. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class IPAddress implements Serializable
{
	private static final long serialVersionUID = -3975747063145993736L;
	
	private String peerKey;
	private String peerName;
	private String ipKey;
	private String ip;
	private int port;
	
	/*
	 * When registering the IP address on the registry server, the constructor is used. 10/02/2018, Bing Li
	 */
	public IPAddress(String peerKey, String peerName, String ip, int port)
	{
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ipKey = Tools.getKeyOfFreeClient(ip, port);
		this.ip = ip;
		this.port = port;
	}

	/*
	 * When multicasting, the peer key is useless. So it is unnecessary to set the value in the constructor. 10/02/2018, Bing Li
	 */
	public IPAddress(String ip, int port)
	{
		this.peerName = UtilConfig.EMPTY_STRING;
		this.ipKey = Tools.getKeyOfFreeClient(ip, port);
		this.ip = ip;
		this.port = port;
	}

	public String getPeerKey()
	{
		return this.peerKey;
	}
	
	/*
	public void setPeerKey(String peerKey)
	{
		this.peerKey = peerKey;
	}
	*/
	
	public String getPeerName()
	{
		return this.peerName;
	}

	/*
	 * The method is used in WindCry, not Wind. 06/20/2022, Bing Li
	 */
	public void setPeerName(String peerName) throws PeerNameIsNullException
	{
		if (peerName != null)
		{
			this.peerName = peerName;
		}
		else
		{
			throw new PeerNameIsNullException(this.ip, this.port);
		}
	}
	
	public String getIPKey()
	{
		return this.ipKey;
	}
	
	public String getIP()
	{
		return this.ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public String toString()
	{
		if (!this.peerName.equals(UtilConfig.EMPTY_STRING))
		{
			return "IPAddress = " + this.peerName + UtilConfig.COLON + this.ip + UtilConfig.COLON + this.port;
		}
		else
		{
			return "IPAddress = " + this.ip + UtilConfig.COLON + this.port;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		IPAddress objIP = (IPAddress)obj;
		return this.ip.equals(objIP.getIP()) && this.port == objIP.getPort();
	}
}
