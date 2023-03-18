package org.greatfree.framework.cs.disabled.broker.message;

import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.container.Response;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public class BrokerNotification extends ClusterNotification
{
	private static final long serialVersionUID = -335819769296084916L;

	private String sourceName;
	private String destinationName;
	private Notification notification;
	private Request request;
	private Response response;

	public BrokerNotification(String sourceName, String destinationName, Notification notification)
	{
//		super(sourceName, DisabledAppID.BROKER_NOTIFICATION);
		super(destinationName, DisabledAppID.BROKER_NOTIFICATION);
		this.sourceName = sourceName;
		this.destinationName = destinationName;
		this.notification = notification;
		this.request = null;
		this.response = null;
	}

	public BrokerNotification(String sourceName, String destinationName, Request request)
	{
//		super(sourceName, DisabledAppID.BROKER_NOTIFICATION);
		super(destinationName, DisabledAppID.BROKER_NOTIFICATION);
		this.sourceName = sourceName;
		this.destinationName = destinationName;
		this.notification = null;
		this.request = request;
		this.response = null;
	}

	public BrokerNotification(String sourceName, String destinationName, Response response)
	{
//		super(sourceName, DisabledAppID.BROKER_NOTIFICATION);
		super(destinationName, DisabledAppID.BROKER_NOTIFICATION);
		this.sourceName = sourceName;
		this.destinationName = destinationName;
		this.notification = null;
		this.request = null;
		this.response = response;
	}
	
	public String getSourceName()
	{
		return this.sourceName;
	}
	
	public String getDestinationName()
	{
		return this.destinationName;
	}
	
	public Notification getNotification()
	{
		return this.notification;
	}
	
	public Request getRequest()
	{
		return this.request;
	}
	
	public Response getResponse()
	{
		return this.response;
	}
}
