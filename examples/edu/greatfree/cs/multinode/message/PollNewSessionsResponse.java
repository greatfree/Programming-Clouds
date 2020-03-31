package edu.greatfree.cs.multinode.message;

import java.util.Set;

import org.greatfree.message.ServerMessage;

/*
 * The response tells the request sender whether a new session is available. If so, the session keys are sent to the request sender. If not, a null message is responded. 04/24/2017, Bing Li
 */

// Created: 04/24/2017, Bing Li
public class PollNewSessionsResponse extends ServerMessage
{
	private static final long serialVersionUID = -4911768419106384745L;
	
	private Set<String> newSessionKeys;

	public PollNewSessionsResponse(Set<String> newSessionKeys)
	{
		super(ChatMessageType.POLL_NEW_SESSIONS_RESPONSE);
		this.newSessionKeys = newSessionKeys;
	}

	public Set<String> getNewSessionKeys()
	{
		return this.newSessionKeys;
	}
}
