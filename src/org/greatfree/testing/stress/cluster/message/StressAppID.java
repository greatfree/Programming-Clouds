package org.greatfree.testing.stress.cluster.message;

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
public final class StressAppID
{
	public final static int STRESS_NOTIFICATION = 80000;

	public final static int STOP_CLUSTER_NOTIFICATION = 80001;
	public final static int STOP_ROOT_NOTIFICATION = 80002;
}
