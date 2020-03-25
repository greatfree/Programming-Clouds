package org.greatfree.message.multicast;

import java.util.List;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

// Created: 10/14/2018, Bing Li
public class RPMulticastResponse extends ServerMessage
{
	private static final long serialVersionUID = 6222789743127981758L;

	private String collaboratorKey;

	private List<MulticastResponse> responses;
	
//	public RPMulticastResponse(int type, String collaboratorKey, List<MulticastResponse> responses)
	public RPMulticastResponse(String collaboratorKey, List<MulticastResponse> responses)
	{
		super(SystemMessageType.RP_MULTICAST_RESPONSE);
		this.collaboratorKey = collaboratorKey;
		this.responses = responses;
	}
	
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
	
	public List<MulticastResponse> getResponses()
	{
		return this.responses;
	}

}
