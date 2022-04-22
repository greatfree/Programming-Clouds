package org.greatfree.message;

/*
 * A new system message configuration is added. It is ThreadingMessageType. Now the maximum number is 56 in that configuration. 09/13/2019, Bing Li 
 * 
 * MulticastMessageType is another configuration that occupies the positions of system messages. Now the maximum number is 44 in that configuration. 09/13/2019, Bing Li
 */

// Created: 04/27/2016, Bing Li
final public class SystemMessageType
{
	public final static int INIT_READ_NOTIFICATION = 3;
	public final static int INIT_READ_FEEDBACK_NOTIFICATION = 4;
	
	public final static int REGISTER_PEER_REQUEST = 5;
	public final static int REGISTER_PEER_RESPONSE = 6;
	
	public final static int PORT_REQUEST = 7;
	public final static int PORT_RESPONSE = 8;
	
	public final static int CLUSTER_IP_REQUEST = 9;
	public final static int CLUSTER_IP_RESPONSE = 10;
	
	public final static int UNREGISTER_PEER_REQUEST = 11;
	public final static int UNREGISTER_PEER_RESPONSE = 12;
	
	public final static int PEER_ADDRESS_REQUEST = 13;
	public final static int PEER_ADDRESS_RESPONSE = 14;
	
	public final static int RP_MULTICAST_RESPONSE = 74;

	public final static int PEER_CHAT_REGISTRY_REQUEST = 75;
	public final static int PEER_CHAT_REGISTRY_RESPONSE = 76;
	
	public final static int PEER_CHAT_PARTNER_REQUEST = 77;
	public final static int PEER_CHAT_PARTNER_RESPONSE = 78;
	
	public final static int IS_ROOT_ONLINE_REQUEST = 79;
	public final static int IS_ROOT_ONLINE_RESPONSE = 80;
	
	public final static int ADD_PARTNER_NOTIFICATION = 81;
	
	public final static int CHAT_NOTIFICATION = 82;
	
	public final static int LEAVE_CLUSTER_NOTIFICATION = 83;
	
	public final static int PARTNERS_REQUEST = 84;
	public final static int PARTNERS_RESPONSE = 85;
	
	public final static int ALL_REGISTERED_IPS_REQUEST = 86;
	public final static int ALL_REGISTERED_IPS_RESPONSE = 87;
	
	public final static int ALL_PEER_NAMES_REQUEST = 88;
	public final static int ALL_PEER_NAMES_RESPONSE = 89;
	
	public final static int SELF_NOTIFICATION = 90;

	public final static int SELF_REQUEST = 91;
	public final static int SELF_RESPONSE = 92;

	public final static int CHAT_REGISTRY_REQUEST = 93;
	public final static int CHAT_REGISTRY_RESPONSE = 94;
	
	public final static int CHAT_PARTNER_REQUEST = 95;
	public final static int CHAT_PARTNER_RESPONSE = 96;
	
	public final static int POLL_NEW_SESSIONS_REQUEST = 97;
	
	public final static int POLL_NEW_CHATS_REQUEST = 98;

	public final static int SHUTDOWN_SERVER_NOTIFICATION = 99;

	public final static int CS_CHAT_REGISTRY_REQUEST = 101;
	public final static int CS_CHAT_REGISTRY_RESPONSE = 102;
	public final static int CS_CHAT_PARTNER_REQUEST = 103;
	public final static int CS_CHAT_PARTNER_RESPONSE = 104;
	
//	public final static int SHUTDOWN_SERVER_NOTIFICATION = 105;
	
	public final static int CS_ADD_PARTNER_NOTIFICATION = 106;
	
//	public final static int POLL_NEW_SESSIONS_REQUEST = 107;
	public final static int POLL_NEW_SESSIONS_RESPONSE = 108;
	
//	public final static int POLL_NEW_CHATS_REQUEST = 109;
	public final static int POLL_NEW_CHATS_RESPONSE = 110;
	
	public final static int CS_CHAT_NOTIFICATION = 111;
	
//	public final static int PEER_CHAT_REGISTRY_REQUEST = 112;
//	public final static int PEER_CHAT_REGISTRY_RESPONSE = 113;
	
//	public final static int PEER_CHAT_PARTNER_REQUEST = 114;
//	public final static int PEER_CHAT_PARTNER_RESPONSE = 115;

	public final static int PEER_ADD_PARTNER_NOTIFICATION = 116;
	
	public final static int PEER_CHAT_NOTIFICATION = 117;
	
	public final static int POLL_SERVER_STATUS_REQUEST = 118;
	public final static int POLL_SERVER_STATUS_RESPONSE = 119;
}
