package org.greatfree.framework.cluster.cs.multinode.unifirst.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.greatfree.chat.ChatMessage;
import org.greatfree.chat.ChatTools;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.multinode.unifirst.message.PollNewChatsRequest;
import org.greatfree.framework.cluster.cs.multinode.unifirst.message.PollNewSessionsRequest;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewChatsResponse;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewSessionsResponse;
import org.greatfree.framework.cluster.cs.twonode.client.ChatClient;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

// Created: 02/15/2019, Bing Li
public class ChatMaintainer
{
	private String localUserKey;
	private String localUsername;
	private String partner;
	private String partnerKey;
	private Set<String> participatedSessions;
	
	private ChatMaintainer()
	{
//		this.participatedSessions = Sets.newHashSet();
		this.participatedSessions = new HashSet<String>();
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static ChatMaintainer instance = new ChatMaintainer();
	
	public static ChatMaintainer UNIFIRST()
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

	public void checkNewSessions() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		CollectedClusterResponse response = (CollectedClusterResponse)ChatClient.CONTAINER().read(new PollNewSessionsRequest(this.localUserKey, this.localUsername));
		List<PollNewSessionsResponse> responses = Tools.filter(response.getResponses(), PollNewSessionsResponse.class);
		for (PollNewSessionsResponse entry : responses)
		{
			if (entry.getNewSessionKeys() != null)
			{
				this.participatedSessions.addAll(entry.getNewSessionKeys());
				System.out.println(entry.getNewSessionKeys().size() + " new chatting sessions are obtained ...");
			}
			else
			{
//				System.out.println("No chatting sessions are obtained ...");
			}
		}
	}
	
  	public void checkNewChats() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		List<PollNewChatsResponse> responses;
		CollectedClusterResponse response;
		PollNewChatsRequest request;
		for (String sessionKey : this.participatedSessions)
		{
			request = new PollNewChatsRequest(sessionKey, this.localUserKey, this.localUsername);
			response = (CollectedClusterResponse)ChatClient.CONTAINER().read(request);
			responses = Tools.filter(response.getResponses(), PollNewChatsResponse.class);
			
			for (PollNewChatsResponse entry : responses)
			{
				/*
				 * The below condition statement is critical. When List<ChatMessage> is null, no any exceptions, e.g., NullPointerException, are returned. I believe that is a bug of JDK. It happens in the Scheduler. I encounter the problem once in the IServer to perform some long tasks in Scheduler. It takes a long time to debug the problem!!! I need to pay much attention to the issue. 02/05/2019, Bing Li
				 */
				if (entry.getMessages() != null)
				{
					if (entry.getMessages().size() > 0)
					{
						System.out.println("PollNewChatsResponse: " + entry.getMessages().size() + " messages received");
						for (ChatMessage message : entry.getMessages())
						{
							System.out.println(message.getSenderName() + " says: " + message.getMessage());
						}
					}
				}
			}
		}
	}
}
