package com.greatfree.multicast;

import java.io.Serializable;

/*
 * The interface returns the broadcast request creator. It is used to constrain the source to provide the method for the resource pool to manage broadcastors. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface RootBroadcastRequestCreatorGettable<Request extends BroadcastRequest, MessagedData extends Serializable>
{
	public RootBroadcastRequestCreatable<Request, MessagedData> getRequestCreator();
}
