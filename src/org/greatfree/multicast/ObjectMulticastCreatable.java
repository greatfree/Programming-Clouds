package org.greatfree.multicast;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;


/*
 * The interface aims to define the methods to create multicast messages to be sent. 11/10/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public interface ObjectMulticastCreatable<Message extends ServerMulticastMessage, MessagedData extends Serializable>
{
	// The interface to create a multicastor with children information. It denotes the node receiving the notification needs to forward the message to those children. 11/26/2014, Bing Li
	public Message createInstanceWithChildren(MessagedData message, HashMap<String, String> childrenMap);
	// The interface to create a multicastor without children information. It represents that the multicasting is ended in the node who receives the message. 11/26/2014, Bing Li
	public Message createInstanceWithoutChildren(MessagedData message);
}
