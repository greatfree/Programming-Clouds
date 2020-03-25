package org.greatfree.cluster.child.container;

import java.io.IOException;
import java.util.Set;

import org.greatfree.cluster.ChildTask;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 09/23/2018, Bing Li
public class ChildServiceProvider
{
	private ChildTask task;
	
	private ChildServiceProvider()
	{
	}
	
	private static ChildServiceProvider instance = new ChildServiceProvider();
	
	public static ChildServiceProvider CHILD()
	{
		if (instance == null)
		{
			instance = new ChildServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(ChildTask task)
	{
		this.task = task;
	}
	
	/*
	 * The method is revised in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void processNotification(Notification notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		/*
		 * The intercasting has to be implemented again with the child-based approach. The below code is not useful. 02/26/2019, Bing Li
		 */
		/*
		 * Some of the code is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
		 */
		/*
		if (notification.getNotificationType() == MulticastMessageType.INTERCAST_NOTIFICATION)
		{
//			Child.CONTAINER().notifyRoot(this.task.processNotificationBySourceChild((IntercastNotification)notification));
			IntercastNotification in = (IntercastNotification)notification;
			InterChildrenNotification icn = this.task.prepareNotification(in);
			if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
				Child.CONTAINER().addPartnerIP(in.getDestinationIP());
				
				Child.CONTAINER().interUnicastNotify(icn);
			}
			else
			{
				Child.CONTAINER().addPartnerIPs(in.getDestinationIPs());
				if (icn.getIntercastNotification().getNotificationType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
				{
					Child.CONTAINER().interBroadcastNotify(icn);
				}
				else
				{
					Child.CONTAINER().interAnycastNotify(icn);
				}
			}
		}
		else
		{
			this.task.processNotification(notification);
		}
		*/
		this.task.processNotification(notification);
		/*
		else if (notification.getNotificationType() == MulticastMessageType.INTERCAST_REQUEST)
		{
//			Child.CONTAINER().notifyRoot(this.task.processSourceRequest((IntercastRequest)notification));
			
			// The intercasting has to be implemented again with the child-based approach. The below code is not useful. 02/26/2019, Bing Li
//			Response response = (Response)Child.CONTAINER().read(this.task.processSourceRequest((IntercastRequest)notification));
		}
		*/
		/*
		else if (notification.getNotificationType() == MulticastMessageType.INTER_CHILD_UNICAST_NOTIFICATION || notification.getNotificationType() == MulticastMessageType.INTER_CHILD_ANYCAST_NOTIFICATION || notification.getNotificationType() == MulticastMessageType.INTER_CHILD_BROADCAST_NOTIFICATION)
		{
			this.task.processDestinationNotification((IntercastChildNotification)notification);
		}
		else
		{
			this.task.processNotification(notification);
		}
		*/
	}
	
	/*
	 * The method is revised in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public MulticastResponse processRequest(Request request)
	{
		/*
		else if (request.getRequestType() == MulticastMessageType.INTER_CHILD_UNICAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_CHILD_ANYCAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_CHILD_BROADCAST_REQUEST)
		{
			return this.task.processDestinationRequest((InterChildrenRequest)request);
		}
		*/
		return this.task.processRequest(request);
	}
	
	public void processIntercastNotification(IntercastNotification in) throws IOException, InterruptedException, DistributedNodeFailedException
	{
//		Child.CONTAINER().notifyRoot(this.task.processNotificationBySourceChild((IntercastNotification)notification));
//		IntercastNotification in = (IntercastNotification)notification;
		InterChildrenNotification icn = this.task.prepareNotification(in);
		if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
		{
			Child.CONTAINER().addPartnerIP(in.getDestinationIP());
			
			/*
			 * The intercast-unicasting should get a new notification, which is processed locally, and the message to the destination child. 03/02/2019, Bing Li
			 */
			Child.CONTAINER().interUnicastNotify(icn);
		}
		else
		{
			Set<IPAddress> ips = in.getDestinationIPs();
			for (IPAddress entry : ips)
			{
				System.out.println("ChildServiceProvider-processIntercastNotification(): " + entry.getIP() + ":" + entry.getPort());
			}
			Child.CONTAINER().addPartnerIPs(in.getDestinationIPs());
//			if (icn.getIntercastNotification().getNotificationType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
			if (icn.getIntercastNotification().getIntercastType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
			{
				Child.CONTAINER().interBroadcastNotify(icn);
			}
			else
			{
				Child.CONTAINER().interAnycastNotify(icn);
			}
		}
	}
	
	public Response processIntercastRequest(IntercastRequest ir) throws DistributedNodeFailedException, IOException
	{
//		InterChildrenRequest icr = this.task.prepareRequest(Child.CONTAINER().getChildIP(), Child.CONTAINER().getChildPort(), ir);
		InterChildrenRequest icr = this.task.prepareRequest(ir);
		icr.setSubRootIP(Child.CONTAINER().getChildIP());
		icr.setSubRootPort(Child.CONTAINER().getChildPort());
		if (!ir.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
		{
			Child.CONTAINER().addPartnerIP(ir.getDestinationIP());
		}
		else
		{
			Child.CONTAINER().addPartnerIPs(ir.getDestinationIPs());
		}
		
		if (ir.getIntercastType() == MulticastMessageType.INTER_UNICAST_REQUEST)
		{
//			System.out.println("ChildServiceProvider-processIntercastRequest(): I am processing INTER_UNICAST_REQUEST ...");
			/*
			 * One problem exists. When performing multicasting, the message types, INTER_BROADCAST_REQUEST or INTER_ANYCAST_REQUEST, are included in the previous multicasting code. So it is required to fix the problem here. 03/12/2019, Bing Li
			 */
			return Child.CONTAINER().interUnicastRead(icr);
		}
		else if (ir.getIntercastType() == MulticastMessageType.INTER_BROADCAST_REQUEST)
		{
			return Child.CONTAINER().interBroadcastRead(icr);
		}
		else
		{
			return Child.CONTAINER().interAnycastRead(icr);
		}
	}

	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		return this.task.processRequest(request);
	}
	
	public void processIntercastResponse(Response res)
	{
		this.task.processResponse(res);
	}

	/*
	public Response processRequestAtRoot(Request request)
	{
		return this.task.processRequestAtRoot(request);
	}
	*/
}
