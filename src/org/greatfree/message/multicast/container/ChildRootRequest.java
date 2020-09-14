package org.greatfree.message.multicast.container;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;

/*
 * The message is sent from a child to its root. It is used for synchronization among children of the cluster. 09/14/2020, Bing Li
 */

// Created: 09/14/2020, Bing Li
public abstract class ChildRootRequest extends MulticastRequest
{
	private static final long serialVersionUID = 5017417217521410300L;
	
	private int applicationID;

	public ChildRootRequest(int applicationID)
	{
		super(MulticastMessageType.CHILD_ROOT_REQUEST);
		this.applicationID = applicationID;
	}

	public int getApplicationID()
	{
		return this.applicationID;
	}
}
