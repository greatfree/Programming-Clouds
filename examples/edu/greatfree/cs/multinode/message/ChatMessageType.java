package edu.greatfree.cs.multinode.message;

//Created: 04/15/2017, Bing Li
public class ChatMessageType
{
	public final static int CS_CHAT_REGISTRY_REQUEST = 10001;
	public final static int CS_CHAT_REGISTRY_RESPONSE = 10002;
	public final static int CS_CHAT_PARTNER_REQUEST = 10003;
	public final static int CS_CHAT_PARTNER_RESPONSE = 10004;
	
	public final static int SHUTDOWN_SERVER_NOTIFICATION = 10005;
	
	public final static int CS_ADD_PARTNER_NOTIFICATION = 10006;
	
	public final static int POLL_NEW_SESSIONS_REQUEST = 10007;
	public final static int POLL_NEW_SESSIONS_RESPONSE = 10008;
	
	public final static int POLL_NEW_CHATS_REQUEST = 10009;
	public final static int POLL_NEW_CHATS_RESPONSE = 10010;
	
	public final static int CS_CHAT_NOTIFICATION = 10011;
	
	public final static int PEER_CHAT_REGISTRY_REQUEST = 10012;
	public final static int PEER_CHAT_REGISTRY_RESPONSE = 10013;
	
	public final static int PEER_CHAT_PARTNER_REQUEST = 10014;
	public final static int PEER_CHAT_PARTNER_RESPONSE = 10015;

	public final static int PEER_ADD_PARTNER_NOTIFICATION = 10016;
	
	public final static int PEER_CHAT_NOTIFICATION = 10017;
	
	public final static int POLL_SERVER_STATUS_REQUEST = 10018;
	public final static int POLL_SERVER_STATUS_RESPONSE = 10019;
}
