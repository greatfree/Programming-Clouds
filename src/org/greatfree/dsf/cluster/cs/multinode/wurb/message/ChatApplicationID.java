package org.greatfree.dsf.cluster.cs.multinode.wurb.message;

// Created: 01/26/2019, Bing Li
public class ChatApplicationID
{
	public final static int POLL_NEW_SESSIONS_REQUEST = 80001;
	public final static int POLL_NEW_SESSIONS_RESPONSE = 80002;

	public final static int POLL_NEW_CHATS_REQUEST = 80003;
	public final static int POLL_NEW_CHATS_RESPONSE = 80004;

	public final static int CHAT_PARTNER_REQUEST = 80005;
	public final static int CHAT_PARTNER_RESPONSE = 80006;
	
	public final static int CHAT_NOTIFICATION = 80007;
	
	public final static int CHAT_REGISTRY_REQUEST = 80008;
	public final static int CHAT_REGISTRY_RESPONSE = 80009;
	
	public final static int ADD_PARTNER_NOTIFICATION = 80010;
	
	public final static int SHUTDOWN_SERVER_NOTIFICATION = 80011;
}

