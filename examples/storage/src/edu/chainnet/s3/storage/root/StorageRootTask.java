package edu.chainnet.s3.storage.root;

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

import edu.chainnet.s3.message.PathsRequest;
import edu.chainnet.s3.message.PathsResponse;
import edu.chainnet.s3.message.S3AppID;
import edu.chainnet.s3.storage.root.table.Paths;

/*
 * The program defines the tasks accomplished by the storage cluster root. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
class StorageRootTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.root");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case S3AppID.SHUTDOWN_STORAGE_CHILDREN_NOTIFICATION:
				log.info("SHUTDOWN_STORAGE_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StorageRoot.STORE().stopChildren();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case S3AppID.SHUTDOWN_STORAGE_ROOT_NOTIFICATION:
				log.info("SHUTDOWN_STORAGE_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StorageRoot.STORE().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		switch (request.getApplicationID())
		{
			case S3AppID.PATHS_REQUEST:
				log.info("PATHS_REQUEST received @" + Calendar.getInstance().getTime());
				PathsRequest pr = (PathsRequest)request;
				return new PathsResponse(Paths.STORE().getPath(pr.getSSSPath(), pr.getFilePath()));
		}
		return null;
	}

}
