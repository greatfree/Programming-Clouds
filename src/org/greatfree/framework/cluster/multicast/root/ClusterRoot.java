package org.greatfree.framework.cluster.multicast.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class ClusterRoot
{
	private ClusterServerContainer server;

	private ClusterRoot()
	{
	}
	
	private static ClusterRoot instance = new ClusterRoot();
	
	public static ClusterRoot CRY()
	{
		if (instance == null)
		{
			instance = new ClusterRoot();
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
	
	public void stopServer(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		this.server.stop(timeout);
	}
	
	public void start(int port, String rootName, String registryIP, int registryPort, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(port, rootName, registryIP, registryPort, task);
		this.server.start();
	}
}
