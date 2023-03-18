package org.greatfree.framework.container.multicast.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.multicast.MultiConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class Root
{
	private ClusterServerContainer server;

	private Root()
	{
	}
	
	private static Root instance = new Root();
	
	public static Root MC()
	{
		if (instance == null)
		{
			instance = new Root();
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

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}

	public void start(String registryIP, int registryPort, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(MultiConfig.ROOT_PORT, MultiConfig.ROOT_NAME, registryIP, registryPort, task);
		this.server.start();
	}

}
