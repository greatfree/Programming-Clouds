package com.greatfree.multicast;

import java.io.Serializable;


/*
 * The interface defines the method that returns the message creator to generate multicast messages. It is used to define the instance of multicastor source. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public interface RootMessageCreatorGettable<Message extends ServerMulticastMessage, ObjectData extends Serializable>
{
	public ObjectMulticastCreatable<Message, ObjectData> getMessageCreator();
}
