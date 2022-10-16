package org.greatfree.framework.p2p.registry;

import org.greatfree.framework.cs.multinode.server.CSAccount;
import org.greatfree.util.UtilConfig;

/*
 * The account of the chatting system for the Peer based chatting. 04/16/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class PeerChatAccount extends CSAccount
{
	private static final long serialVersionUID = 5779563597983984084L;

	private String preference;
	
	public PeerChatAccount(String userKey, String userName, String description, String preference)
	{
		super(userKey, userName, description);
		this.preference = preference;
	}

	public PeerChatAccount(String userKey, String userName)
	{
		super(userKey, userName, UtilConfig.EMPTY_STRING);
		this.preference = UtilConfig.EMPTY_STRING;
	}
	
	public String getPreference()
	{
		return this.preference;
	}
}
