package org.greatfree.framework.cluster.multicast.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 06/21/2022
 *
 */
public class HelloInterBroadcastResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8093211317583392618L;

	/*
	 * Since it is possible that multiple destinations map to one child, it is required for the child to generate multiple messages. Each destination corresponds to one message. 06/21/2022, Bing Li
	 */
//	private String message;
	private List<String> messages;

	public HelloInterBroadcastResponse(List<String> messages, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_INTER_BROADCAST_RESPONSE, collaboratorKey);
		this.messages = messages;
	}

	public List<String> getMessages()
	{
		return this.messages;
	}
}
