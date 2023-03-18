package org.greatfree.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * The account keeps the information about signed-in peer. 05/01/2017, Bing Li
 */

// Created: 05/01/2017, Bing Li
public class PeerAccount
{
	private String peerKey;
	private String peerName;
	private String ip;
	private int peerPort;
//	private int adminPort;
//	private List<Integer> ports;
	private Map<String, Integer> otherPorts;
	private boolean isServerDisabled;
//	private boolean isClientDisabled;
	
//	public PeerAccount(String peerKey, String peerName, String ip, int peerPort, int adminPort)
	public PeerAccount(String peerKey, String peerName, String ip, int peerPort)
	{
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.peerPort = peerPort;
//		this.adminPort = adminPort;
//		this.ports = new ArrayList<Integer>();
//		this.ports.add(peerPort);
		this.otherPorts = new ConcurrentHashMap<String, Integer>();
		this.isServerDisabled = false;
//		this.isClientDisabled = false;
	}

//	public PeerAccount(String peerKey, String peerName, String ip, int peerPort, boolean isSD, boolean isCD)
	public PeerAccount(String peerKey, String peerName, String ip, int peerPort, boolean isSD)
	{
		this.peerKey = peerKey;
		this.peerName = peerName;
		this.ip = ip;
		this.peerPort = peerPort;
		this.otherPorts = new ConcurrentHashMap<String, Integer>();
		this.isServerDisabled = isSD;
//		this.isClientDisabled = isCD;
	}

	public String getPeerKey()
	{
		return this.peerKey;
	}
	
	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String getIP()
	{
		return this.ip;
	}

	public int getPeerPort()
	{
		return this.peerPort;
	}
	
	public void addPort(String key, int port)
	{
		this.otherPorts.put(key, port);
	}
	
	public int getPort(String key)
	{
		return this.otherPorts.get(key);
	}
	
	public Collection<Integer> getOtherPorts()
	{
		return this.otherPorts.values();
	}

	/*
	public int getAdminPort()
	{
		return this.adminPort;
	}
	
	public List<Integer> getPorts()
	{
		return this.ports;
	}
	*/
	
	public boolean isServerDisabled()
	{
		return this.isServerDisabled;
	}

	/*
	public boolean isClientDisabled()
	{
		return this.isClientDisabled;
	}
	*/
}
