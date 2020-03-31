package edu.greatfree.container.p2p.message;

import org.greatfree.message.container.Request;
import org.greatfree.util.UtilConfig;

// Created: 01/12/2019, Bing Li
public class ChatRegistryRequest extends Request
{
	private static final long serialVersionUID = -3147744027195440794L;

	private String peerID;
	private String peerName;
	private String peerDescription;
	private String preference;

	public ChatRegistryRequest(String peerID, String peerName, String peerDescription, String preference)
	{
		super(P2PChatApplicationID.PEER_CHAT_REGISTRY_REQUEST);
		this.peerID = peerID;
		this.peerName = peerName;
		this.peerDescription = peerDescription;
		this.preference = preference;
	}

	public ChatRegistryRequest(String peerID)
	{
		super(P2PChatApplicationID.PEER_CHAT_REGISTRY_REQUEST);
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
