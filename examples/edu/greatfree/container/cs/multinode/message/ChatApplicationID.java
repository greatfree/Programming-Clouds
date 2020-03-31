package edu.greatfree.container.cs.multinode.message;

// Created: 12/31/2018, Bing Li
public class ChatApplicationID
{
	public final static int CHAT_REGISTRY_REQUEST = 90001;
	public final static int CHAT_REGISTRY_RESPONSE = 90002;
	
	public final static int CHAT_PARTNER_REQUEST = 90003;
	public final static int CHAT_PARTNER_RESPONSE = 90004;
	
	public final static int ADD_PARTNER_NOTIFICATION = 90005;
	
	public final static int POLL_NEW_SESSIONS_REQUEST = 90006;
	
	public final static int POLL_NEW_CHATS_REQUEST = 90007;

	public final static int CHAT_NOTIFICATION = 90008;
	
	public final static int SHUTDOWN_SERVER_NOTIFICATION = 90009;
}
