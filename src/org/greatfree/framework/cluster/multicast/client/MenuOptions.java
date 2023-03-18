package org.greatfree.framework.cluster.multicast.client;

/**
 * 
 * @author libing
 * 
 * 10/02/2022
 *
 */
final class MenuOptions
{
	public final static int NO_OPTION = -1;

	public final static int BROADCAST_NOTIFICATION = 1;
	public final static int ANYCAST_NOTIFICATION = 2;
	public final static int UNICAST_NOTIFICATION = 3;
	public final static int BROADCAST_REQUEST = 4;
	public final static int ANYCAST_REQUEST = 5;
	public final static int UNICAST_REQUEST = 6;

	public final static int INTER_BROADCAST_NOTIFICATION = 7;
	public final static int INTER_ANYCAST_NOTIFICATION = 8;
	public final static int INTER_UNICAST_NOTIFICATION = 9;
	public final static int INTER_BROADCAST_REQUEST = 10;
	public final static int INTER_ANYCAST_REQUEST = 11;
	public final static int INTER_UNICAST_REQUEST = 12;

	public final static int QUIT = 0;
}
