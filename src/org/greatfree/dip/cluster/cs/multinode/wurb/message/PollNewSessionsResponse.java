package org.greatfree.dip.cluster.cs.multinode.wurb.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 01/27/2019, Bing Li
public class PollNewSessionsResponse extends MulticastResponse
{
	private static final long serialVersionUID = -853967514949180387L;

	private Set<String> newSessionKeys;

	public PollNewSessionsResponse(Set<String> newSessionKeys, String collaboratorKey)
	{
		super(ChatApplicationID.POLL_NEW_SESSIONS_RESPONSE, collaboratorKey);
		this.newSessionKeys = newSessionKeys;
	}

	public Set<String> getNewSessionKeys()
	{
		return this.newSessionKeys;
	}
}
