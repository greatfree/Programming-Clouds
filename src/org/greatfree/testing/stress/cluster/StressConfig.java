package org.greatfree.testing.stress.cluster;

import org.greatfree.server.container.PeerContainer;

/**
 * 
 * @author libing
 *
 * 11/07/2021, Bing Li
 * 
 * I notice that when messages are sent as notifications too frequently, the server-side/cluster-side cannot process responsively. The server might be dead for that.
 * 
 * That is normal. But I need to check whether some improper designs exist in the server/cluster.
 *
 */

// Created: 11/07/2021, Bing Li
public class StressConfig
{
	public final static String STRESS_ROOT_NAME = "StressRoot";
	public final static String STRESS_ROOT_KEY = PeerContainer.getPeerKey(STRESS_ROOT_NAME);
	
	public final static int STREE_ROOT_PORT = 8910;
	
	public final static String HOME = "/Temp/";
	
//	public final static int ITERATION_SIZE = 10000;
//	public final static int ITERATION_SIZE = 100000;
//	public final static int ITERATION_SIZE = 500000;
	public final static int ITERATION_SIZE = 5000000;
}
