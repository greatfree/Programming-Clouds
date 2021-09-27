package org.greatfree.cluster.message;

// Created: 09/23/2018, Bing Li
public final class ClusterMessageType
{
	public final static int JOIN_NOTIFICATION = 24;
	public final static int LEAVE_NOTIFICATION = 25;
	
	public final static int CHILD_RESPONSE = 26;
	
	public final static int IS_ROOT_ONLINE_REQUEST = 27;
	public final static int IS_ROOT_ONLINE_RESPONSE = 28;

	public final static int HEAVY_WORKLOAD_NOTIFICATION = 219;
	public final static int SELECTED_CHILD_NOTIFICATION = 220;
	public final static int SUPERFLUOUS_RESOURCES_NOTIFICATION = 221;
	
	public final static int PARTITION_SIZE_REQUEST = 222;
	public final static int PARTITION_SIZE_RESPONSE = 223;
	
	public final static int SET_STATES_NOTIFICATION = 224;
	
	public final static int CLUSTER_SIZE_REQUEST = 225;
	public final static int CLUSTER_SIZE_RESPONSE = 226;
	
	public final static int ADDITIONAL_CHILDREN_REQUEST = 227;
	public final static int ADDITIONAL_CHILDREN_RESPONSE = 228;
	
	public final static int PATH_REQUEST = 229;
	public final static int PATH_RESPONSE = 230;
}
