package edu.chainnet.sc.collaborator;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.SCConfig;

/*
 * The collaborator is implemented using a hierarchy-based cluster. The program is the root of the cluster. 10/16/2020, Bing Li
 */

// Created: 10/16/2020, Bing Li
class Collaborator
{
	private ClusterServerContainer server;

	private Collaborator()
	{
	}
	
	private static Collaborator instance = new Collaborator();
	
	public static Collaborator BC()
	{
		if (instance == null)
		{
			instance = new Collaborator();
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
		Paths.DB().dispose();
		this.server.stop(timeout);
	}
	
	public void start(String registryIP, int registryPort, String dbHome, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		Paths.DB().init(dbHome);
		this.server = new ClusterServerContainer(SCConfig.COLLABORATOR_ROOT_PORT, SCConfig.COLLABORATOR_ROOT_NAME, registryIP, registryPort, task);
		this.server.start();
	}
}
