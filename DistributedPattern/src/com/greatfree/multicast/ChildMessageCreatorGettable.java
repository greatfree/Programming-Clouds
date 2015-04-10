package com.greatfree.multicast;

/*
 * The interface defines the method that returns the message creator to generate multicast messages to children. It is used to define the instance of children multicastor source. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public interface ChildMessageCreatorGettable<Message extends ServerMulticastMessage>
{
	public ChildMulticastMessageCreatable<Message> getMessageCreator();
}
