package org.greatfree.framework.cluster.multicast.child;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.multicast.message.ClusterAppID;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloAnycastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloBroadcastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterAnycastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterBroadcastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloInterUnicastResponse;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastNotification;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastRequest;
import org.greatfree.framework.cluster.multicast.message.HelloUnicastResponse;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloAnyNotification;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloAnyRequest;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloBroadNotification;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloBroadRequest;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloUniNotification;
import org.greatfree.framework.cluster.multicast.message.InterChildrenHelloUniRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
final class ClusterChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cluster.multicast.child");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.HELLO_BROADCAST_NOTIFICATION:
				log.info("HELLO_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloBroadcastNotification hbn = (HelloBroadcastNotification)notification;
				log.info(hbn.getMessage());
				break;
				
			case ClusterAppID.HELLO_ANYCAST_NOTIFICATION:
				log.info("HELLO_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloAnycastNotification han = (HelloAnycastNotification)notification;
				log.info(han.getMessage());
				break;
				
			case ClusterAppID.HELLO_UNICAST_NOTIFICATION:
				log.info("HELLO_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloUnicastNotification hun = (HelloUnicastNotification)notification;
				log.info(hun.getMessage());
				break;

			case ClusterAppID.SHUTDOWN_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterChild.CRY().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | NoSuchPaddingException | IOException | InterruptedException | RemoteReadException
						| DistributedNodeFailedException | RemoteIPNotExistedException | PeerNameIsNullException e)
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
			case ClusterAppID.HELLO_BROADCAST_REQUEST:
				log.info("HELLO_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloBroadcastRequest hbr = (HelloBroadcastRequest)request;
				log.info(hbr.getMessage());
				return new HelloBroadcastResponse(hbr.getMessage() + Tools.generateUniqueKey(), hbr.getCollaboratorKey());
				
			case ClusterAppID.HELLO_ANYCAST_REQUEST:
				log.info("HELLO_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloAnycastRequest har = (HelloAnycastRequest)request;
				log.info(har.getMessage());
				return new HelloAnycastResponse(har.getMessage() + Tools.generateUniqueKey(), har.getCollaboratorKey());
				
			case ClusterAppID.HELLO_UNICAST_REQUEST:
				log.info("HELLO_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloUnicastRequest hur = (HelloUnicastRequest)request;
				log.info(hur.getMessage());
				return new HelloUnicastResponse(hur.getMessage() + Tools.generateUniqueKey(), hur.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.HELLO_INTER_BROADCAST_NOTIFICATION:
				log.info("HELLO_INTER_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterBroadcastNotification hibn = (HelloInterBroadcastNotification)notification;
				log.info(hibn.getMessage());
				return new InterChildrenHelloBroadNotification(hibn, "I am crazy at all!");

			case ClusterAppID.HELLO_INTER_UNICAST_NOTIFICATION:
				log.info("HELLO_INTER_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterUnicastNotification hiun = (HelloInterUnicastNotification)notification;
				log.info(hiun.getMessage());
				return new InterChildrenHelloUniNotification(hiun, "Please let me in");
				
			case ClusterAppID.HELLO_INTER_ANYCAST_NOTIFICATION:
				log.info("HELLO_INTER_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterAnycastNotification hian = (HelloInterAnycastNotification)notification;
				log.info(hian.getMessage());
				return new InterChildrenHelloAnyNotification(hian, "How to avoid nucleic acid?");
		}
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		switch (request.getApplicationID())
		{
			case ClusterAppID.HELLO_INTER_UNICAST_REQUEST:
				log.info("HELLO_INTER_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterUnicastRequest hiur = (HelloInterUnicastRequest)request;
				log.info(hiur.getMessage());
				return new InterChildrenHelloUniRequest(hiur, "Do not waste time any more");
				
			case ClusterAppID.HELLO_INTER_ANYCAST_REQUEST:
				log.info("HELLO_INTER_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterAnycastRequest hiar = (HelloInterAnycastRequest)request;
				log.info(hiar.getMessage());
				return new InterChildrenHelloAnyRequest(hiar, "I got angry!");
				
			case ClusterAppID.HELLO_INTER_BROADCAST_REQUEST:
				log.info("HELLO_INTER_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterBroadcastRequest hibr = (HelloInterBroadcastRequest)request;
				log.info(hibr.getMessage());
				return new InterChildrenHelloBroadRequest(hibr, "The university is shit!");
		}
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		int size;
		List<String> msgs;
		switch (request.getApplicationID())
		{
			case ClusterAppID.HELLO_INTER_UNICAST_REQUEST:
				log.info("HELLO_INTER_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloUniRequest ichur = (InterChildrenHelloUniRequest)request;
				log.info(ichur.getAdditionalMessage());
				HelloInterUnicastRequest hiur = (HelloInterUnicastRequest)ichur.getIntercastRequest();
				log.info(hiur.getMessage());
				return new HelloInterUnicastResponse(hiur.getMessage() + Tools.generateUniqueKey(), request.getCollaboratorKey());
				
			case ClusterAppID.HELLO_INTER_ANYCAST_REQUEST:
				log.info("HELLO_INTER_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloAnyRequest ichar = (InterChildrenHelloAnyRequest)request;
				log.info(ichar.getAdditionalMessage());
				HelloInterAnycastRequest hiar = (HelloInterAnycastRequest)ichar.getIntercastRequest();
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hiar.getChildDestinations().get(ClusterChild.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hiar.getMessage());
				}
				msgs = new ArrayList<String>();
				for (int i = 0; i < size; i++)
				{
					msgs.add(hiar.getMessage() + Tools.generateUniqueKey());
				}
				return new HelloInterAnycastResponse(msgs, request.getCollaboratorKey());
				
			case ClusterAppID.HELLO_INTER_BROADCAST_REQUEST:
				log.info("HELLO_INTER_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloBroadRequest ichbr = (InterChildrenHelloBroadRequest)request;
				log.info(ichbr.getAdditionalMessage());
				HelloInterBroadcastRequest hibr = (HelloInterBroadcastRequest)ichbr.getIntercastRequest();
				log.info(hibr.getMessage());
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hibr.getChildDestinations().get(ClusterChild.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hibr.getMessage());
				}
				msgs = new ArrayList<String>();
				for (int i = 0; i < size; i++)
				{
					msgs.add(hibr.getMessage() + Tools.generateUniqueKey());
				}
				return new HelloInterBroadcastResponse(msgs, request.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}
