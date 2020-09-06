package org.greatfree.dsf.cluster.cs.multinode.intercast.group.client;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.greatfree.chat.ChatTools;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.clusterserver.child.GroupChatMessage;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.PollGroupChatRequest;
import org.greatfree.dsf.cluster.cs.multinode.intercast.group.message.PollGroupChatResponse;
import org.greatfree.dsf.cluster.cs.twonode.client.ChatClient;
import org.greatfree.dsf.cs.multinode.server.CSAccount;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

// Created: 04/06/2019, Bing Li
class ChatMaintainer
{
	private boolean isGroupCreator;
	
	private String localUserKey;
	private String localUsername;
	
	private String groupName;
	private String groupKey;
	
	private String oneMemberKey;
	private String oneMemberName;
	
	private Set<String> memberKeys;
	
	private Set<String> displayedChats;

	private ChatMaintainer()
	{
		this.isGroupCreator = false;
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static ChatMaintainer instance = new ChatMaintainer();
	
	public static ChatMaintainer GROUP()
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

	public void init(String username, String groupName)
	{
		this.localUsername = username;
		this.localUserKey = ChatTools.getUserKey(username);
		this.groupName = groupName;
		this.groupKey = ChatTools.getUserKey(groupName);
		this.memberKeys = Sets.newHashSet();
		this.displayedChats = Sets.newHashSet();
	}
	
	public boolean isGroupCreator()
	{
		return this.isGroupCreator;
	}
	
	public void setIsGroupCreator(boolean isGroupCreator)
	{
		this.isGroupCreator = isGroupCreator;
	}
	
	public String getLocalUserKey()
	{
		return this.localUserKey;
	}
	
	public String getLocalUsername()
	{
		return this.localUsername;
	}
	
	public void setOneMemberName(String oneMemberName)
	{
		this.oneMemberName = oneMemberName;
		this.oneMemberKey = ChatTools.getUserKey(oneMemberName);
	}
	
	public String getOneMemberName()
	{
		return this.oneMemberName;
	}
	
	public String getOneMemberKey()
	{
		return this.oneMemberKey;
	}

	public String getGroupKey()
	{
		return this.groupKey;
	}

	public String getGroupName()
	{
		return this.groupName;
	}
	
	public Set<String> getMembers()
	{
		return this.memberKeys;
	}
	
	public void addMember(CSAccount account)
	{
		this.memberKeys.add(account.getUserKey());
	}
	
	public void checkNewChats() throws ClassNotFoundException, RemoteReadException, IOException
	{
		List<PollGroupChatResponse> responses;
		PollGroupChatRequest request = new PollGroupChatRequest(this.localUserKey, this.groupKey, ChatConfig.MESSAGE_COUNT, ChatConfig.CHAT_MESSAGE_OBTAINED_PERIOD, this.localUsername);
		Response response = (Response)ChatClient.CONTAINER().read(request);
		responses = Tools.filter(response.getResponses(), PollGroupChatResponse.class);
		for (PollGroupChatResponse entry : responses)
		{
			if (entry.getMessages() != null)
			{
				if (entry.getMessages().size() > 0)
				{
					for (GroupChatMessage message : entry.getMessages())
					{
						if (!this.displayedChats.contains(message.getKey()))
						{
//							System.out.println("PollGroupChatResponse: " + entry.getMessages().size() + " messages received");
							this.displayedChats.add(message.getKey());
							System.out.println(message.getSenderName() + " says: " + message.getMessage());
						}
					}
				}
			}
		}
	}
}
