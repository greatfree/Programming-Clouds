package org.greatfree.message;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * The interface aims to define the methods to create multicast messages to be sent. 11/10/2014, Bing Li
 */

// Created: 05/12/2017, Bing Li
public interface RootBroadcastNotificationCreatable<Notification extends OldMulticastMessage, NotificationData extends Serializable>
{
	// The interface to create a notification with children information. It denotes the node receiving the notification needs to forward the message to those children. 11/26/2014, Bing Li
	public Notification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NotificationData message);
	// The interface to create a notification without children information. It represents that the multicasting is ended in the node who receives the message. 11/26/2014, Bing Li
	public Notification createInstanceWithoutChildren(NotificationData message);
}
