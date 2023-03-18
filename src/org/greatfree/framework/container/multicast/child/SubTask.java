package org.greatfree.framework.container.multicast.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.framework.container.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.framework.container.multicast.message.MultiAppID;
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
 * 05/09/2022
 *
 */
final class SubTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.container.multicast.child");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case MultiAppID.HELLO_WORLD_BROADCAST_NOTIFICATION:
				log.info("HELLO_WORLD_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldBroadcastNotification hwbn = (HelloWorldBroadcastNotification)notification;
				log.info(hwbn.getHelloWorld().getHelloWorld());
				break;
				
			case MultiAppID.HELLO_WORLD_ANYCAST_NOTIFICATION:
				log.info("HELLO_WORLD_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldAnycastNotification hwan = (HelloWorldAnycastNotification)notification;
				log.info(hwan.getHelloWorld().getHelloWorld());
				break;
				
			case MultiAppID.HELLO_WORLD_UNICAST_NOTIFICATION:
				log.info("HELLO_WORLD_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldUnicastNotification hwun = (HelloWorldUnicastNotification)notification;
				log.info(hwun.getHelloWorld().getHelloWorld());
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Child.MC().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.STOP_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Child.MC().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
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
			case MultiAppID.HELLO_WORLD_BROADCAST_REQUEST:
				log.info("HELLO_WORLD_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldBroadcastRequest hwbr = (HelloWorldBroadcastRequest)request;
				return new HelloWorldBroadcastResponse(hwbr.getHelloWorld(), hwbr.getCollaboratorKey());
				
			case MultiAppID.HELLO_WORLD_ANYCAST_REQUEST:
				log.info("HELLO_WORLD_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldAnycastRequest hwar = (HelloWorldAnycastRequest)request;
				return new HelloWorldAnycastResponse(hwar.getHelloWorld(), hwar.getCollaboratorKey());
				
			case MultiAppID.HELLO_WORLD_UNICAST_REQUEST:
				log.info("HELLO_WORLD_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldUnicastRequest hwur = (HelloWorldUnicastRequest)request;
				return new HelloWorldUnicastResponse(hwur.getHelloWorld(), hwur.getCollaboratorKey());
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
