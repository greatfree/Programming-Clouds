package org.greatfree.message.multicast;

/*'
 *  A new system message configuration is added. It is ThreadingMessageType. Now the maximum number is 56 in that configuration. 09/13/2019, Bing Li
 *   
 * MulticastMessageType is another configuration that occupies the positions of system messages. Now the maximum number is 15 in that configuration. 09/13/2019, Bing Li
 */

// Created: 01/14/2019, Bing Li
public class MulticastMessageType
{
	public final static int NOTIFICATION = 15;
	public final static int REQUEST = 16;
	public final static int RESPONSE = 17;
	
	public final static int BROADCAST_NOTIFICATION = 18;
	public final static int ANYCAST_NOTIFICATION = 19;
	public final static int UNICAST_NOTIFICATION = 20;
	public final static int LOCAL_NOTIFICATION = 29;
	
	public final static int BROADCAST_REQUEST = 21;
	public final static int ANYCAST_REQUEST = 22;
	public final static int UNICAST_REQUEST = 23;
	public final static int LOCAL_REQUEST = 30;
	
	public final static int ROOT_IPADDRESS_BROADCAST_NOTIFICATION = 208;

	public final static int INTERCAST_NOTIFICATION = 32;
	public final static int INTERCAST_REQUEST = 33;
	
	public final static int INTER_UNICAST_NOTIFICATION = 34;
	public final static int INTER_ANYCAST_NOTIFICATION = 35;
	public final static int INTER_BROADCAST_NOTIFICATION = 36;
	
	public final static int INTER_UNICAST_REQUEST = 37;
	public final static int INTER_ANYCAST_REQUEST = 38;
	public final static int INTER_BROADCAST_REQUEST = 39;

	
	/*
	public final static int INTER_CHILD_UNICAST_NOTIFICATION = 40;
	public final static int INTER_CHILD_UNICAST_REQUEST = 41;
	
	public final static int INTER_CHILD_BROADCAST_NOTIFICATION = 42;
	public final static int INTER_CHILD_BROADCAST_REQUEST = 43;
	
	public final static int INTER_CHILD_ANYCAST_NOTIFICATION = 44;
	public final static int INTER_CHILD_ANYCAST_REQUEST = 45;
	*/
	
	/*
	 * The code is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public final static int INTER_CHILDEN_NOTIFICATION = 40;
	public final static int INTER_CHILDREN_REQUEST = 41;

	public final static int BROADCAST_RESPONSE = 42;
	public final static int ANYCAST_RESPONSE = 43;
	public final static int UNICAST_RESPONSE = 44;
	
	public final static int CHILD_ROOT_REQUEST = 72;
	public final static int CHILD_ROOT_RESPONSE = 73;
}
