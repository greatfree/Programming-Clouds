package edu.chainnet.s3.pool.root;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.s3.message.S3AppID;

/*
 * The tasks taken by the root is simple. That has nothing different from a regular cluster. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
class PoolRootTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.pool.root");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case S3AppID.SHUTDOWN_POOL_CHILDREN_NOTIFICATION:
				log.info("SHUTDOWN_POOL_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PoolRoot.STORE().stopChildren();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case S3AppID.SHUTDOWN_POOL_ROOT_NOTIFICATION:
				log.info("SHUTDOWN_POOL_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PoolRoot.STORE().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public Response processRequest(Request arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
