package edu.chainnet.center.coordinator;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.center.coordinator.manage.Paths;

/**
 * 
 * @author libing
 *
 * 04/22/2021, Bing Li
 *
 */
class DataCoordinator
{
	private ClusterServerContainer server;

	private DataCoordinator()
	{
	}
	
	private static DataCoordinator instance = new DataCoordinator();
	
	public static DataCoordinator CENTER()
	{
		if (instance == null)
		{
			instance = new DataCoordinator();
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
		Paths.CENTER().dispose();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		Paths.CENTER().init(CenterConfig.CONFIG_PATH);
		this.server = new ClusterServerContainer(port, CenterConfig.CENTER_COORDINATOR, task);
		this.server.start();
	}
}

