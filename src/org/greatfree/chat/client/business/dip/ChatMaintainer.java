package org.greatfree.chat.client.business.dip;

import java.util.Set;

import org.greatfree.chat.ChatMessage;
import org.greatfree.chat.ChatTools;
import org.greatfree.framework.cs.multinode.message.PollNewChatsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsResponse;

import com.google.common.collect.Sets;

/*
 * The class checks whether new sessions or new chat messages are available on the chatting server. 04/24/2017, Bing Li
 */

// Created: 04/24/2017, Bing Li
public class ChatMaintainer
{
	private String localUserKey;
	private String localUsername;
	private String partner;
	private String partnerKey;
	private Set<String> participatedSessions;
	
	private ChatMaintainer()
	{
//		this.localUserKey = Tools.generateUniqueKey();
		this.participatedSessions = Sets.newHashSet();
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
		this.participatedSessions.clear();
		this.participatedSessions = null;
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

	/*
	 * Add the participated chat session. 04/24/2017, Bing Li
	 */
	public void addSession(String sessionKey)
	{
		this.participatedSessions.add(sessionKey);
	}

	/*
	 * Check whether new sessions are available on the chatting server. 04/24/2017, Bing Li
	 */
	public void checkNewSessions()
	{
		PollNewSessionsResponse response = ChatReader.RR().checkNewSessions(this.localUserKey, this.localUsername);
		if (response.getNewSessionKeys() != null)
		{
			System.out.println(response.getNewSessionKeys().size() + " new chatting sessions are obtained ...");
			this.participatedSessions.addAll(response.getNewSessionKeys());
			System.out.println(response.getNewSessionKeys().size() + " new chatting sessions are retained ...");
		}
	}

	/*
	 * Check whether new chatting messages are available on the chatting server. 04/24/2017, Bing Li
	 */
	public void checkNewChats()
	{
		PollNewChatsResponse response;
		for (String sessionKey : this.participatedSessions)
		{
			response = ChatReader.RR().checkNewChats(sessionKey, this.localUserKey, this.localUsername);
//			System.out.println("PollNewChatsResponse: received");
//			System.out.println("PollNewChatsResponse: " + response.getMessages() + " messages received");
			if (response.getMessages().size() > 0)
			{
				System.out.println("PollNewChatsResponse: " + response.getMessages().size() + " messages received");
//				for (ChatMessage message : response.getMessages().values())
//				for (String message : response.getMessages())
				for (ChatMessage message : response.getMessages())
				{
					System.out.println(message.getSenderName() + " says: " + message.getMessage());
				}
			}
		}
	}
}
