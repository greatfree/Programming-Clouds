package org.greatfree.framework.cs.disabled.broker.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 03/17/2023
 *
 */
public class PollResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8473273443339759966L;
	
	private List<BrokerNotification> messages;

	public PollResponse(List<BrokerNotification> messages, String collaboratorKey)
	{
		super(DisabledAppID.POLL_RESPONSE, collaboratorKey);
		this.messages = messages;
	}
	
	public List<BrokerNotification> getMessages()
	{
		return this.messages;
	}
}
