package org.greatfree.framework.container.p2p.message;

import java.util.Map;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

// Created: 09/26/2019, Bing Li
public class AllPeerNamesResponse extends ServerMessage
{
	private static final long serialVersionUID = -6549099428881268007L;
	
	private Map<String, String> names;

	public AllPeerNamesResponse(Map<String, String> names)
	{
		super(SystemMessageType.ALL_PEER_NAMES_RESPONSE);
		this.names = names;
	}

	public Map<String, String> getAllNames()
	{
		return this.names;
	}
}
