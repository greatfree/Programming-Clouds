package org.greatfree.testing.stress.cluster.root;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.testing.stress.cluster.message.StressAppID;

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
class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.testing.stress.cluster.root");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case StressAppID.STOP_CLUSTER_NOTIFICATION:
				log.info("STOP_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StressRoot.ROOT().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case StressAppID.STOP_ROOT_NOTIFICATION:
				log.info("STOP_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StressRoot.ROOT().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
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
