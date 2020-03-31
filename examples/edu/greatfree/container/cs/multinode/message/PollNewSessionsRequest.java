package edu.greatfree.container.cs.multinode.message;

import org.greatfree.message.container.Request;

// Created: 01/06/2019, Bing Li
public class PollNewSessionsRequest extends Request
{
	private static final long serialVersionUID = -1434010744237515546L;
	
	private String receiverKey;
	private String username;

	public PollNewSessionsRequest(String receiverKey, String username)
	{
		super(ChatApplicationID.POLL_NEW_SESSIONS_REQUEST);
		this.receiverKey = receiverKey;
		this.username = username;
	}

	public String getReceiverKey()
	{
		return this.receiverKey;
	}
	
	public String getUsername()
	{
		return this.username;
	}
}
