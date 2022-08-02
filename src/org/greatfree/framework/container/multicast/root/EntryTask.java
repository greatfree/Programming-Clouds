package org.greatfree.framework.container.multicast.root;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.multicast.message.MultiAppID;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class EntryTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.container.multicast.root");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case MultiAppID.STOP_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Root.MC().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.STOP_ROOT_NOTIFICATION:
				log.info("STOP_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Root.MC().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
