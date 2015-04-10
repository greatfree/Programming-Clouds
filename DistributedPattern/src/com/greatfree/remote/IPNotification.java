package com.greatfree.remote;

import com.greatfree.multicast.ServerMessage;

/*
 * This is an object to contain the instance of IPPort and the message to be sent to it. It is used by the class of Eventer in most time. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class IPNotification<Notification extends ServerMessage>
{
	// The message to be sent to the IP/port included in the object. 11/20/2014, Bing Li
	private Notification notification;
	// The IP/port, to which the notification to be sent. 11/20/2014, Bing Li
	private IPPort ipPort;

	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
	public IPNotification(IPPort ipPort, Notification notification)
	{
		this.ipPort = ipPort;
		this.notification = notification;
	}

	/*
	 * Expose the notification. 11/20/2014, Bing Li
	 */
	public Notification getNotification()
	{
		return this.notification;
	}

	/*
	 * Expose the instance of IPPort. 11/20/2014, Bing Li
	 */
	public IPPort getIPPort()
	{
		return this.ipPort;
	}
}
