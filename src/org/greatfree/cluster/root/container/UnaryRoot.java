package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public final class UnaryRoot
{
	private ClusterServerContainer server;

	private UnaryRoot()
	{
	}
	
	private static UnaryRoot instance = new UnaryRoot();
	
	public static UnaryRoot CLUSTER()
	{
		if (instance == null)
		{
			instance = new UnaryRoot();
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
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(String rootName, int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(port, rootName, task);
		this.server.start();
	}
	
	public void start(String rootName, int port, String registryIP, int registryPort, RootTask task, boolean isBroker) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(port, rootName, registryIP, registryPort, task, isBroker);
		this.server.start();
	}
}
