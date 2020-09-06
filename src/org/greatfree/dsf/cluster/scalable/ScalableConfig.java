package org.greatfree.dsf.cluster.scalable;

import org.greatfree.util.Tools;

// Created: 09/05/2020, Bing Li
public class ScalableConfig
{
	public final static String POOL_CLUSTER_ROOT = "PoolClusterRoot";
	public final static String POOL_CLUSTER_ROOT_KEY = Tools.getHash(ScalableConfig.POOL_CLUSTER_ROOT);
	public final static int POOL_CLUSTER_PORT = 8940;

	public final static String TASK_CLUSTER_ROOT = "TaskClusterRoot";
	public final static String TASK_CLUSTER_ROOT_KEY = Tools.getHash(ScalableConfig.TASK_CLUSTER_ROOT);
	public final static int TASK_CLUSTER_PORT = 8939;
}
