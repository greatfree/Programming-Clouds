package org.greatfree.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.chat.ChatConfig;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/*
 * All of the signed-in peers must be retained in the registry. 05/01/2017, Bing Li
 * 
 * As a demonstration, it is fine. For a practical system, the design is critical and tough and it should be extended. 05/01/2017, Bing Li
 * 
 */

// Created: 05/01/2017, Bing Li
public class PeerRegistry
{
	// Declare a map to keep all of the accounts. 11/09/2014, Bing Li
	private Map<String, PeerAccount> accounts;

	// Busy ports are retained in the collection. 05/01/2017, Bing Li
	private Map<String, Set<Integer>> busyPorts;

	/*
	 * Initialize. 11/09/2014, Bing Li
	 */
	private PeerRegistry()
	{
		// Define a synchronized list for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, PeerAccount>();
		// Initialize the busy ports collection. 05/01/2017, Bing Li
		this.busyPorts = new ConcurrentHashMap<String, Set<Integer>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static PeerRegistry instance = new PeerRegistry();

	public static PeerRegistry SYSTEM()
	{
		if (instance == null)
		{
			instance = new PeerRegistry();
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
		this.accounts.clear();
		this.accounts = null;
		
		this.busyPorts.clear();
		this.busyPorts = null;
	}

	/*
	 * Decide whether a peer is registered. 05/30/2017, Bing Li
	 */
	public boolean isExisted(String peerKey)
	{
		return this.accounts.containsKey(peerKey);
	}

	/*
	 * Get one registered peer. 05/30/2017, Bing Li
	 */
	public PeerAccount get(String peerKey)
	{
		if (this.accounts.containsKey(peerKey))
		{
			return this.accounts.get(peerKey);
		}
		return UtilConfig.NO_PEER_ACCOUNT;
	}
	
	public IPAddress getAddress(String peerKey)
	{
		PeerAccount account = this.get(peerKey);
		if (account != UtilConfig.NO_PEER_ACCOUNT)
		{
			return new IPAddress(peerKey, account.getIP(), account.getPeerPort());
		}
		return UtilConfig.NO_IP_ADDRESS;
	}

	/*
	 * Get all of the IP addresses registered in the registry. 05/30/2017, Bing Li
	 */
	public Map<String, IPAddress> getIPPorts()
	{
		Map<String, IPAddress> ips = new HashMap<String, IPAddress>();
		for (PeerAccount entry : this.accounts.values())
		{
			if (!entry.getPeerKey().equals(ChatConfig.CHAT_REGISTRY_SERVER_KEY))
			{
				ips.put(entry.getPeerKey(), new IPAddress(entry.getPeerKey(), entry.getIP(), entry.getPeerPort()));
			}
		}
		return ips;
	}

	public Map<String, IPAddress> getIPPorts(List<String> peerKeys)
	{
		Map<String, IPAddress> ips = new HashMap<String, IPAddress>();
		for (String entry : peerKeys)
		{
			if (this.accounts.containsKey(entry))
			{
				ips.put(entry, new IPAddress(entry, this.accounts.get(entry).getIP(), this.accounts.get(entry).getPeerPort()));
			}
		}
		return ips;
	}

	public Map<String, String> getNames()
	{
		Map<String, String> names = new HashMap<String, String>();
		for (PeerAccount entry : this.accounts.values())
		{
			if (!entry.getPeerKey().equals(ChatConfig.CHAT_REGISTRY_SERVER_KEY))
			{
				names.put(entry.getPeerKey(), entry.getPeerName());
			}
		}
		return names;
	}
	
	public Map<String, IPAddress> getIPPorts(Set<String> peerKeys)
	{
		Map<String, IPAddress> ips = new HashMap<String, IPAddress>();
		for (String key : peerKeys)
		{
			ips.put(key, new IPAddress(key, this.accounts.get(key).getIP(), this.accounts.get(key).getPeerPort()));
		}
		return ips;
	}

	/*
	 * Register one peer. 05/30/2017, Bing Li
	 */
	// public void register(String peerKey, String peerName, String ip, int port, int serverCount)
	public synchronized int register(String peerKey, String peerName, String ip, int port)
	{
		if (!this.accounts.containsKey(peerKey))
		{
			String ipKey = Tools.getHash(ip);
			if (!this.busyPorts.containsKey(ipKey))
			{
				Set<Integer> ports = Sets.newHashSet();
				ports.add(port);
				this.busyPorts.put(ipKey, ports);
			}
			else
			{
				port = this.getIdlePort(ipKey, port);
			}
			// this.accounts.put(peerKey, new PeerAccount(peerKey, peerName, ip, port, ++port));
			this.accounts.put(peerKey, new PeerAccount(peerKey, peerName, ip, port));
		}
//		System.out.println("PeerRegistry-register(): port = " + port);
		return port;
	}

	/*
	 * Register other ports. 05/02/2017, Bing Li
	 */
	public synchronized void registeOthers(String peerKey, String portKey, String ip, int port)
	{
		this.accounts.get(peerKey).addPort(portKey, port);
		this.busyPorts.get(Tools.getHash(ip)).add(port);
	}

	/*
	 * Unregister one peer. 05/20/2017, Bing Li
	 */
	public synchronized void unregister(String peerKey)
	{
		PeerAccount account = this.accounts.get(peerKey);
		String ipKey = Tools.getHash(account.getIP());
		System.out.println("removed port = " + account.getPeerPort());
		this.busyPorts.get(ipKey).remove(account.getPeerPort());
		for (Integer port : account.getOtherPorts())
		{
			System.out.println("removed port = " + port);
			this.busyPorts.get(ipKey).remove(port);
		}
		if (this.busyPorts.get(ipKey).isEmpty())
		{
			this.busyPorts.remove(ipKey);
		}
		this.accounts.remove(peerKey);
	}

	/*
	 * Get the idle port for the port conflicted peer. 05/02/2017, Bing Li
	 */
	public synchronized int getIdlePort(String peerKey, String portKey, String ip, int port)
	{
//		System.out.println("1) getIdlePort(): ip = " + ip);
		String ipKey = Tools.getHash(ip);
		int idlePort = this.getIdlePort(ipKey, port);
		this.accounts.get(peerKey).addPort(portKey, idlePort);
		return idlePort;
	}

	/*
	 * Get the idle port for the port conflicted peer. 05/02/2017, Bing Li
	 */
	private int getIdlePort(String ipKey, int port)
	{
		// System.out.println("2) getIdlePort(): ipKey = " + ipKey);
		/*
		System.out.println("2) getIdlePort(): port size = " + this.busyPorts.get(ipKey).size());
		for (Integer entry : this.busyPorts.get(ipKey))
		{
			System.out.println("2) getIdlePort(): port = " + entry);
		}
		*/
		if (this.busyPorts.get(ipKey).contains(port))
		{
			port += this.busyPorts.get(ipKey).size();
			while (this.busyPorts.get(ipKey).contains(port))
			{
				port++;
			}
		}
		this.busyPorts.get(ipKey).add(port);
		return port;
	}
}
