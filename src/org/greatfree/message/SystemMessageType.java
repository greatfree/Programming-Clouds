package org.greatfree.message;

/*
 * A new system message configuration is added. It is ThreadingMessageType. Now the maximum number is 56 in that configuration. 09/13/2019, Bing Li 
 * 
 * MulticastMessageType is another configuration that occupies the positions of system messages. Now the maximum number is 44 in that configuration. 09/13/2019, Bing Li
 */

// Created: 04/27/2016, Bing Li
public class SystemMessageType
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
	
	public final static int RP_MULTICAST_RESPONSE = 15;
}
