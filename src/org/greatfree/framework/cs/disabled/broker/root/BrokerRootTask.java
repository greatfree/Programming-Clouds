package org.greatfree.framework.cs.disabled.broker.root;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.UnaryRoot;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.disabled.broker.message.DisabledAppID;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
final class BrokerRootTask implements RootTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.disabled.broker.root");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case DisabledAppID.SHUTDOWN_ROOT_NOTIFICATION:
				log.info("SHUTDOWN_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					UnaryRoot.CLUSTER().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | InterruptedException | RemoteReadException | IOException
						| RemoteIPNotExistedException e)
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
