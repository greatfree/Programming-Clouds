package org.greatfree.framework.cluster.multicast.root;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.multicast.message.ClusterAppID;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cluster.multicast.root");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.SHUTDOWN_ROOT_NOTIFICATION:
				log.info("SHUTDOWN_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterRoot.CRY().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | InterruptedException | RemoteReadException | IOException
						| RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;

				/*
			case ClusterAppID.SHUTDOWN_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterRoot.CRY().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				*/
		}
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
