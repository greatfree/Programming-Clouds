package org.greatfree.framework.cs.disabled.broker.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.UnaryChild;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.disabled.broker.message.BrokerNotification;
import org.greatfree.framework.cs.disabled.broker.message.DisabledAppID;
import org.greatfree.framework.cs.disabled.broker.message.PollRequest;
import org.greatfree.framework.cs.disabled.broker.message.PollResponse;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
final class BrokerChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.disabled.broker.child");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case DisabledAppID.BROKER_NOTIFICATION:
				log.info("BROKER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				BrokerNotification bn = (BrokerNotification)notification;
				MessageRepository.DB().addNotification(bn);
				break;
				
			case DisabledAppID.SHUTDOWN_CHILDREN_NOTIFICATION:
				log.info("SHUTDOWN_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					UnaryChild.CLUSTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
	public MulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getApplicationID())
		{
			case DisabledAppID.POLL_REQUEST:
				log.info("POLL_REQUEST received @" + Calendar.getInstance().getTime());
				PollRequest pr = (PollRequest)request;
				return new PollResponse(MessageRepository.DB().getNotifications(pr.getDestinationName()), pr.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}
}
