package org.greatfree.dsf.multicast.rp.message;

// Created: 10/21/2018, Bing Li
public class MulticastDIPRPMessageType
{
	public final static int HELLO_WORLD_BROADCAST_NOTIFICATION = 201;
	public final static int HELLO_WORLD_ANYCAST_NOTIFICATION = 202;
	public final static int HELLO_WORLD_UNICAST_NOTIFICATION = 203;
	
	public final static int SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION = 204;
	public final static int SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION = 205;
	
	public final static int HELLO_WORLD_BROADCAST_REQUEST = 206;
	public final static int HELLO_WORLD_BROADCAST_RESPONSE = 207;
	
//	public final static int ROOT_IPADDRESS_BROADCAST_NOTIFICATION = 208;
	
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
