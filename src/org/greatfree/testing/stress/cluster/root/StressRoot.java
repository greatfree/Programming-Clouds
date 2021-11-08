package org.greatfree.testing.stress.cluster.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.testing.stress.cluster.StressConfig;

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
class StressRoot
{
	private ClusterServerContainer server;

	private StressRoot()
	{
	}
	
	private static StressRoot instance = new StressRoot();
	
	public static StressRoot ROOT()
	{
		if (instance == null)
		{
			instance = new StressRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
	}
	
	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.server.stop(timeout);
	}
	
	public void start(int port, String registryIP, int registryPort, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.server = new ClusterServerContainer(port, StressConfig.STRESS_ROOT_NAME, registryIP, registryPort, task);
		this.server.start();
	}
}
