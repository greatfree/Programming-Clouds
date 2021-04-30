package edu.chainnet.s3.edsa.root;

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
 * The EDSA server focuses on data encoding/decoding. 07/11/2020, Bing Li
 * 
 * The program defines the tasks to be accomplished by the root of the EDSA cluster. 07/10/2020, Bing Li
 */

// Created: 07/10/2020, Bing Li
class EDSARootTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.s3.edsa.root");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case S3AppID.SHUTDOWN_EDSA_CHILDREN_NOTIFICATION:
				log.info("SHUTDOWN_EDSA_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					EDSARoot.EDSA().stopChildren();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case S3AppID.SHUTDOWN_EDSA_ROOT_NOTIFICATION:
				log.info("SHUTDOWN_EDSA_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					EDSARoot.EDSA().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
