package edu.chainnet.center.coordinator;

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

import edu.chainnet.center.coordinator.manage.Paths;
import edu.chainnet.center.message.CenterApplicationID;
import edu.chainnet.center.message.DataPathsRequest;
import edu.chainnet.center.message.DataPathsResponse;

// Created: 04/22/2021, Bing Li
class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.center.coordinator");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		switch (request.getApplicationID())
		{
			case CenterApplicationID.DATA_PATHS_REQUEST:
				log.info("DATA_PATHS_REQUEST received @" + Calendar.getInstance().getTime());
				DataPathsRequest dpr = (DataPathsRequest)request;
				return new DataPathsResponse(Paths.CENTER().getPath(dpr.getDocPath(), dpr.getIndexPath(), dpr.getCachePath()));
		}
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CenterApplicationID.STOP_CENTER_CHILDREN_NOTIFICATION:
				log.info("STOP_CENTER_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					DataCoordinator.CENTER().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
	
			case CenterApplicationID.STOP_CENTER_COORDINATOR_NOTIFICATION:
				log.info("STOP_CENTER_COODINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					DataCoordinator.CENTER().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
		// TODO Auto-generated method stub
		return null;
	}

}
