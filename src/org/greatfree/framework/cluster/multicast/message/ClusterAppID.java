package org.greatfree.framework.cluster.multicast.message;

/**
 * 
 * @author libing
 * 
 * 03/09/2023
 *
 */
public final class ClusterAppID
{
	public final static int HELLO_BROADCAST_NOTIFICATION = 80000;
	public final static int HELLO_ANYCAST_NOTIFICATION = 80001;
	public final static int HELLO_UNICAST_NOTIFICATION = 80002;

	public final static int HELLO_BROADCAST_REQUEST = 80003;
	public final static int HELLO_BROADCAST_RESPONSE = 80004;

	public final static int HELLO_ANYCAST_REQUEST = 80005;
	public final static int HELLO_ANYCAST_RESPONSE = 80006;

	public final static int HELLO_UNICAST_REQUEST = 80007;
	public final static int HELLO_UNICAST_RESPONSE = 80008;
	
	public final static int SHUTDOWN_CHILDREN_NOTIFICATION = 80009;
	public final static int SHUTDOWN_ROOT_NOTIFICATION = 80010;
	
	public final static int HELLO_INTER_UNICAST_NOTIFICATION = 80011;
	public final static int HELLO_INTER_ANYCAST_NOTIFICATION = 80012;
	public final static int HELLO_INTER_BROADCAST_NOTIFICATION = 80013;
	
	public final static int HELLO_INTER_UNICAST_REQUEST = 80014;
	public final static int HELLO_INTER_UNICAST_RESPONSE = 80015;
	
	public final static int HELLO_INTER_ANYCAST_REQUEST = 80016;
	public final static int HELLO_INTER_ANYCAST_RESPONSE = 80017;
	
	public final static int HELLO_INTER_BROADCAST_REQUEST = 80018;
	public final static int HELLO_INTER_BROADCAST_RESPONSE = 80019;
}
