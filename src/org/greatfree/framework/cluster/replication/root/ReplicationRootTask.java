package org.greatfree.framework.cluster.replication.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.replication.message.ReplicationApplicationID;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 09/07/2020, Bing Li
class ReplicationRootTask implements RootTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ReplicationApplicationID.STOP_REPLICATION_CLUSTER_NOTIFICATION:
				try
				{
					ReplicationRoot.REPLICATED().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ReplicationApplicationID.STOP_REPLICATION_ROOT_NOTIFICATION:
				try
				{
					ReplicationRoot.REPLICATED().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
