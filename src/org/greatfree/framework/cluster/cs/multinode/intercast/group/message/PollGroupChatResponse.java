package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import java.util.List;

import org.greatfree.framework.cluster.cs.multinode.intercast.group.clusterserver.child.GroupChatMessage;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/05/2019, Bing Li
public class PollGroupChatResponse extends MulticastResponse
{
	private static final long serialVersionUID = 7356557118543178169L;

	public List<GroupChatMessage> messages;

	public PollGroupChatResponse(List<GroupChatMessage> messages, String collaboratorKey)
	{
		super(GroupChatApplicationID.POLL_GROUP_CHAT_RESPONSE, collaboratorKey);
		this.messages = messages;
	}

	public List<GroupChatMessage> getMessages()
	{
		return this.messages;
	}
}
