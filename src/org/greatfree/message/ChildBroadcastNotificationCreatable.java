package org.greatfree.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * The interface defines the method to create a multicast message that is transmitted from the current local node to its children rather than from the root one. 11/10/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
public interface ChildBroadcastNotificationCreatable<Notification extends OldMulticastMessage>
{
	public Notification createInstanceWithChildren(HashMap<String, IPAddress> children, Notification msg);
}
