package org.greatfree.multicast;

import java.io.Serializable;

import org.greatfree.message.abandoned.AnycastRequest;

/*
 * The interface returns the anycast request creator. It is used to constrain the source and provide the method for the resource pool to manage anycastors. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface RootAnyRequestCreatable<Request extends AnycastRequest, MessagedData extends Serializable>
{
	public RootAnycastRequestCreatable<Request, MessagedData> getRequestCreator();
}
