package edu.chainnet.sc.collaborator;

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

import edu.chainnet.sc.message.PathsResponse;
import edu.chainnet.sc.message.SCAppID;

/*
 * The tasks of the collaborator are defined in the program. 
 */

// Created: 10/16/2020, Bing Li
class CollaboratorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.root");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		switch (request.getApplicationID())
		{
			case SCAppID.PATHS_REQUEST:
				log.info("PATHS_REQUEST received @" + Calendar.getInstance().getTime());
				return new PathsResponse(Paths.DB().getPath());
		}
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case SCAppID.STOP_COLLABORATOR_CLUSTER_NOTIFICATION:
				log.info("STOP_COLLABORATOR_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Collaborator.BC().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case SCAppID.STOP_COLLABORATOR_ROOT_NOTIFICATION:
				log.info("STOP_COLLABORATOR_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Collaborator.BC().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public Response processRequest(Request request)
	{
		return null;
	}

}
