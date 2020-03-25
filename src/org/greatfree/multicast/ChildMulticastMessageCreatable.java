package org.greatfree.multicast;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * The interface defines the method to create a multicast message on a child node rather than the root one. 11/10/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public interface ChildMulticastMessageCreatable<Message extends ServerMulticastMessage>
{
	public Message createInstanceWithChildren(Message msg, HashMap<String, String> children);
}
