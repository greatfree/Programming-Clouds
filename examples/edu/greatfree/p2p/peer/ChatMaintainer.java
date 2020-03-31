package edu.greatfree.p2p.peer;

import org.greatfree.util.UtilConfig;

import edu.greatfree.cs.multinode.ChatTools;

/*
 * The class maintains the partner and the local user for the chatting. 04/24/2017, Bing Li
 */

// Created: 05/02/2017, Bing Li
public class ChatMaintainer
{
	private String localUserKey;
	private String localUsername;
	private String partner;
	private String partnerKey;
	private String partnerIP;
	private int partnerPort;
	
	private ChatMaintainer()
	{
		this.partnerIP = null;
		this.partnerPort = UtilConfig.ZERO;
	}

	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static ChatMaintainer instance = new ChatMaintainer();
	
	public static ChatMaintainer PEER()
	{
		if (instance == null)
		{
			instance = new ChatMaintainer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 04/17/2017, Bing Li
	 */
	public void dispose()
	{
	}

	public void init(String username, String partner)
	{
		this.localUsername = username;
		this.localUserKey = ChatTools.getUserKey(username);
		this.partner = partner;
		this.partnerKey = ChatTools.getUserKey(partner);
	}
	
	public String getLocalUserKey()
	{
		return this.localUserKey;
	}
	
	public String getLocalUsername()
	{
		return this.localUsername;
	}
	
	public String getPartner()
	{
		return this.partner;
	}
	
	public String getPartnerKey()
	{
		return this.partnerKey;
	}
	
	public void setPartnerIP(String ip)
	{
		this.partnerIP = ip;
	}
	
	public String getPartnerIP()
	{
		return this.partnerIP;
	}
	
	public void setPartnerPort(int port)
	{
		this.partnerPort = port;
	}

	public int getPartnerPort()
	{
		return this.partnerPort;
	}
}
