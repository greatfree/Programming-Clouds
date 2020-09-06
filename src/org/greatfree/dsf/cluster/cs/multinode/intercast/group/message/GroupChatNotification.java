package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 04/05/2019, Bing Li
public class GroupChatNotification extends IntercastNotification
{
	private static final long serialVersionUID = -1692920932089430997L;
	
	private String groupKey;
	private String senderKey;
	private Set<String> userKeys;
	
	private String chatMessage;

	private String senderName;

	public GroupChatNotification(String groupKey, String senderKey, Set<String> userKeys, String chatMessage, String senderName)
	{
		super(MulticastMessageType.INTER_BROADCAST_NOTIFICATION, groupKey, userKeys, GroupChatApplicationID.GROUP_CHAT_NOTIFICATION);
		this.senderKey = senderKey;
		this.userKeys = userKeys;
//		this.userKeys.add(senderKey);
		this.chatMessage = chatMessage;
		this.groupKey = groupKey;

		this.senderName = senderName;
	}
	
	public String getGroupKey()
	{
		return this.groupKey;
	}

	public String getSenderKey()
	{
		return this.senderKey;
	}
	
	public Set<String> getUserKeys()
	{
		return this.userKeys;
	}
	
	public String getChatMessage()
	{
		return this.chatMessage;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
