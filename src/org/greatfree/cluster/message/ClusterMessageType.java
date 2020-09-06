package org.greatfree.cluster.message;

// Created: 09/23/2018, Bing Li
public class ClusterMessageType
{
	public final static int JOIN_NOTIFICATION = 24;
	public final static int LEAVE_NOTIFICATION = 25;
	
	public final static int CHILD_RESPONSE = 26;
	
	public final static int IS_ROOT_ONLINE_REQUEST = 27;
	public final static int IS_ROOT_ONLINE_RESPONSE = 28;

	public final static int HEAVY_WORKLOAD_NOTIFICATION = 219;
	public final static int SELECTED_CHILD_NOTIFICATION = 220;
	public final static int SUPERFLUOUS_RESOURCES_NOTIFICATION = 221;
}
