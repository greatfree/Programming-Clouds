package org.greatfree.dsf.cluster.replication;

import org.greatfree.util.Tools;

// Created: 09/07/2020, Bing Li
public class ReplicationConfig
{
	public final static int REPLICAS = 2;
	
	public final static String REPLICATION_ROOT = "ReplicationRoot";
	public final static String REPLICATION_ROOT_KEY = Tools.getHash(REPLICATION_ROOT);
	public final static int REPLICATION_ROOT_PORT = 8938;
}
