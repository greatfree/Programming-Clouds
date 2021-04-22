package org.greatfree.framework.multicast;

import org.greatfree.server.container.PeerContainer;
import org.greatfree.util.IPAddress;

/*
 * The constants about cluster multicasting are defined in the class. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class MulticastConfig
{
//	public final static String CLUSTR_ROOT_IP = "192.168.1.111";
	
	// The name should be unique. 01/15/2019, Bing Li
	public final static String CLUSTER_SERVER_ROOT_NAME = "ClusterServerRoot";
//	public final static String CLUSTER_SERVER_ROOT_KEY = Tools.getHash(CLUSTER_SERVER_ROOT_NAME);
	public final static String CLUSTER_SERVER_ROOT_KEY = PeerContainer.getPeerKey(CLUSTER_SERVER_ROOT_NAME);

	// The name should be unique. 01/15/2019, Bing Li
	public final static String CLUSTER_CLIENT_ROOT_NAME = "ClusterClientRoot";
//	public final static String CLUSTER_CLIENT_ROOT_KEY = Tools.getHash(CLUSTER_CLIENT_ROOT_NAME);
	public final static String CLUSTER_CLIENT_ROOT_KEY = PeerContainer.getPeerKey(CLUSTER_CLIENT_ROOT_NAME);

	public final static int MULTICASTOR_POOL_SIZE = 100;
	public final static long RESOURCE_WAIT_TIME = 2000;
	
//	public final static long BROADCAST_REQUEST_WAIT_TIME = 5000;
	public final static long BROADCAST_REQUEST_WAIT_TIME = 50000;
//	public final static long BROADCAST_REQUEST_WAIT_TIME = 10000;
	
	public final static int ROOT_BRANCH_COUNT = 2;
//	public final static int ROOT_BRANCH_COUNT = 1;
	public final static int SUB_BRANCH_COUNT = 2;
//	public final static int SUB_BRANCH_COUNT = 1;
	
	public static IPAddress CLUSTER_ROOT_ADDRESS = null;
}
