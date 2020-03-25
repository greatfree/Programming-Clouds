package org.greatfree.dip.cs.twonode.client;

import org.greatfree.chat.ChatTools;

/*
 * The class checks whether new sessions or new chat messages are available on the chatting server. For the two-node C/S DIP, its feature is minimized to create user keys. 06/17/2018, Bing Li
 */

// Created: 06/17/2018, Bing Li
public class ChatMaintainer
{
	private String localUserKey;
	private String localUsername;
	private String partner;
	private String partnerKey;
	
	private ChatMaintainer()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static ChatMaintainer instance = new ChatMaintainer();
	
	public static ChatMaintainer CS()
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
}
