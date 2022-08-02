package org.greatfree.framework.container.multicast.client;

/**
 * 
 * @author libing
 * 
 * 05/10/2022
 *
 */
final class MultiOptions
{
	public final static int NO_OPTION = -1;

	public final static int BROADCAST_NOTIFICATION = 1;
	public final static int ANYCAST_NOTIFICATION = 2;
	public final static int UNICAST_NOTIFICATION = 3;
	public final static int BROADCAST_REQUEST = 4;
	public final static int ANYCAST_REQUEST = 5;
	public final static int UNICAST_REQUEST = 6;
	public final static int STOP_CHILDREN = 7;
	public final static int STOP_ROOT = 8;
	public final static int STOP_REGISTRY = 9;
	public final static int QUIT = 0;
}
