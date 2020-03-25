package org.greatfree.multicast;

import java.io.Serializable;

import org.greatfree.message.abandoned.BroadcastRequest;

/*
 * The interface returns the broadcast request creator. It is used to constrain the source to provide the method for the resource pool to manage broadcastors. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface RootBroadRequestCreatable<Request extends BroadcastRequest, MessagedData extends Serializable>
{
	public RootBroadcastRequestCreatable<Request, MessagedData> getRequestCreator();
}
