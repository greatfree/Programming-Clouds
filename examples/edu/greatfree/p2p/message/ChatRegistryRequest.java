package edu.greatfree.p2p.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

import edu.greatfree.cs.multinode.message.ChatMessageType;

/*
 * The message encloses the data to register one account in the chatting system. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class ChatRegistryRequest extends ServerMessage
{
	private static final long serialVersionUID = -3265857560426666644L;
	
	private String peerID;
	private String peerName;
	private String peerDescription;
	private String preference;

	public ChatRegistryRequest(String peerID, String peerName, String peerDescription, String preference)
	{
		super(ChatMessageType.PEER_CHAT_REGISTRY_REQUEST);
		this.peerID = peerID;
		this.peerName = peerName;
		this.peerDescription = peerDescription;
		this.preference = preference;
	}

	public ChatRegistryRequest(String peerID)
	{
		super(ChatMessageType.PEER_CHAT_REGISTRY_REQUEST);
		this.peerID = peerID;
		this.peerName = UtilConfig.EMPTY_STRING;
		this.peerDescription = UtilConfig.EMPTY_STRING;
		this.preference = UtilConfig.EMPTY_STRING;
	}
	
	public String getPeerID()
	{
		return this.peerID;
	}
	
	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String getPeerDescription()
	{
		return this.peerDescription;
	}
	
	public String getPreference()
	{
		return this.preference;
	}
}
