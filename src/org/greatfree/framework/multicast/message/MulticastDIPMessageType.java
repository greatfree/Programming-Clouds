package org.greatfree.framework.multicast.message;

/*
 * The message type is defined as integer constants in the class. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public final class MulticastDIPMessageType
{
	public final static int HELLO_WORLD_BROADCAST_NOTIFICATION = 201;
	public final static int HELLO_WORLD_ANYCAST_NOTIFICATION = 202;
	public final static int HELLO_WORLD_UNICAST_NOTIFICATION = 203;
	
	public final static int SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION = 204;
	public final static int SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION = 205;
	
	public final static int HELLO_WORLD_BROADCAST_REQUEST = 206;
	public final static int HELLO_WORLD_BROADCAST_RESPONSE = 207;
	
	public final static int HELLO_WORLD_ANYCAST_REQUEST = 209;
	public final static int HELLO_WORLD_ANYCAST_RESPONSE = 210;
	
	public final static int HELLO_WORLD_UNICAST_REQUEST = 211;
	public final static int HELLO_WORLD_UNICAST_RESPONSE = 212;
	
	public final static int CLIENT_HELLO_WORLD_BROADCAST_REQUEST = 213;
	public final static int CLIENT_HELLO_WORLD_BROADCAST_RESPONSE = 214;
	
	public final static int CLIENT_HELLO_WORLD_ANYCAST_REQUEST = 215;
	public final static int CLIENT_HELLO_WORLD_ANYCAST_RESPONSE = 216;
	
	public final static int CLIENT_HELLO_WORLD_UNICAST_REQUEST = 217;
	public final static int CLIENT_HELLO_WORLD_UNOCAST_RESPONSE = 218;
}
