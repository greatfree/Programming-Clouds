package org.greatfree.framework.cluster.cs.multinode.wurb.client;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.chat.ChatMessage;
import org.greatfree.chat.ChatTools;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewChatsRequest;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewChatsResponse;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewSessionsRequest;
import org.greatfree.framework.cluster.cs.multinode.wurb.message.PollNewSessionsResponse;
import org.greatfree.framework.cluster.cs.twonode.client.ChatClient;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

// Created: 01/28/2019, Bing Li
class ChatMaintainer
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
	
	public static ChatMaintainer CLUSTER_CONTAINER()
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

	public void checkNewSessions() throws ClassNotFoundException, RemoteReadException, IOException
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
	
  	public void checkNewChats() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<PollNewChatsResponse> responses;
		CollectedClusterResponse response;
		PollNewChatsRequest request;
		for (String sessionKey : this.participatedSessions)
		{
			request = new PollNewChatsRequest(sessionKey, this.localUserKey, this.localUsername);
//			response = (Response)ChatClient.CONTAINER().read(new PollNewChatsRequest(sessionKey, this.localUserKey, this.localUsername));
			
//			System.out.println("1) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey());
			
			response = (CollectedClusterResponse)ChatClient.CONTAINER().read(request);
//			System.out.println("2) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response is received!");
//			System.out.println("1) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response.getResponses() size = " + response.getResponses().size());
			responses = Tools.filter(response.getResponses(), PollNewChatsResponse.class);
//			System.out.println("2.5) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response is received!");
//			System.out.println("1) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s responses' size " + responses.size() + " after filtering ...");
			
//			MulticastResponse res = response.getResponses().get(0);
//			System.out.println("1.5) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s multicast response collaboratorKey = " + res.getCollaboratorKey());
			
			/*
			for (MulticastResponse entry : response.getResponses())
			{
				System.out.println("1.5) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s multicast response collaboratorKey = " + entry.getCollaboratorKey());
				PollNewChatsResponse res = (PollNewChatsResponse)entry;
				System.out.println("1.55) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey());
				System.out.println("1.551) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response's messages' size = " + res.getCollaboratorKey());
				System.out.println("1.6) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response's messages' size = " + res.getMessages().size());
			}
			*/
			
			/*
			PollNewChatsResponse chatRes = responses.get(0);
			System.out.println("1.5) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s chatRes[0] messages' size = " + chatRes.getMessages().size());
			chatRes = responses.get(1);
			System.out.println("1.6) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s chatRes[1] messages' size = " + chatRes.getMessages().size());
			chatRes = responses.get(2);
			System.out.println("1.7) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s chatRes[2] messages' size = " + chatRes.getMessages().size());
			chatRes = responses.get(3);
			System.out.println("1.8) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s chatRes[3] messages' size = " + chatRes.getMessages().size());
			*/
			
			for (PollNewChatsResponse entry : responses)
			{
				/*
				 * The below condition statement is critical. When List<ChatMessage> is null, no any exceptions, e.g., NullPointerException, are returned. I believe that is a bug of JDK. It happens in the Scheduler. I encounter the problem once in the IServer to perform some long tasks in Scheduler. It takes a long time to debug the problem!!! I need to pay much attention to the issue. 02/05/2019, Bing Li
				 */
				if (entry.getMessages() != null)
				{
//					System.out.println("2) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s entry messages' size " + entry.getMessages().size());
					if (entry.getMessages().size() > 0)
					{
						System.out.println("PollNewChatsResponse: " + entry.getMessages().size() + " messages received");
						for (ChatMessage message : entry.getMessages())
						{
							System.out.println(message.getSenderName() + " says: " + message.getMessage());
						}
					}
//					System.out.println("3) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s entry messages' size " + entry.getMessages().size());
				}
			}
//			System.out.println("4) ChatMaintainer-checkNewChats(): collaboratorKey = " + request.getCollaboratorKey() + "'s response is received!");
		}
	}
}
