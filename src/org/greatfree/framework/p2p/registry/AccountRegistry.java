package org.greatfree.framework.p2p.registry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.greatfree.util.IPAddress;

/*
 * This is an account registry. All of the accounts of the chatting system are retained in it. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class AccountRegistry
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.p2p.registry");

	// Declare a map to retain the account that joins the P2P chatting. 07/20/2017, Bing Li
	private Map<String, PeerChatAccount> accounts;
	
	private AccountRegistry()
	{
		// Define a concurrent map for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, PeerChatAccount>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static AccountRegistry instance = new AccountRegistry();
	
	public static AccountRegistry APP()
	{
		if (instance == null)
		{
			instance = new AccountRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.accounts.clear();
		this.accounts = null;
	}
	
	public boolean isAccountExisted(String userKey)
	{
		return this.accounts.containsKey(userKey);
	}
	
	public void add(PeerChatAccount account)
	{
		log.info("account, " + account.getUserName() + " added to AccountRegistry!");
		
		/*
		 * Since the registry is managed by the internal system, it is safe to overwrite existing ones. The method is also called by low-level peer registry to simplify registry of the multicasting children. 10/14/2022, Bing Li
		 */
		this.accounts.put(account.getUserKey(), account);
		/*
		if (!this.accounts.containsKey(account.getUserKey()))
		{
			this.accounts.put(account.getUserKey(), account);
		}
		*/
	}
	
	public void remove(String userKey)
	{
		this.accounts.remove(userKey);
	}

	public Collection<PeerChatAccount> getAllAccounts()
	{
		return this.accounts.values();
	}
	
	public PeerChatAccount getAccount(String key)
	{
		if (this.accounts.containsKey(key))
		{
			return this.accounts.get(key);
		}
		return null;
	}

	/*
	 * The input is the IP addresses obtained from the PeerRegistry. However it must contain some IPs that do not participate the application level. So they should be excluded from the IP addresses that are related to the application. 05/30/2017, Bing Li
	 */
	// Updated in XTU. 05/17/2017, Bing Li
	public Map<String, IPAddress> getIPPorts(Map<String, IPAddress> ipPorts)
	{
//		System.out.println("AccountRegistry-getIPPorts(): init ipPorts size = " + ipPorts.size());
//		Set<String> nonPeerKeys = Sets.newHashSet();
		Set<String> nonPeerKeys = new HashSet<String>();
		for (String ipEntry : ipPorts.keySet())
		{
			if (!this.accounts.containsKey(ipEntry))
			{
				nonPeerKeys.add(ipEntry);
			}
		}
//		System.out.println("AccountRegistry-getIPPorts(): nonPeerKeys size = " + nonPeerKeys.size());
		for (String key : nonPeerKeys)
		{
			ipPorts.remove(key);
		}
//		System.out.println("AccountRegistry-getIPPorts(): final ipPorts size = " + ipPorts.size());
		return ipPorts;
	}
}
