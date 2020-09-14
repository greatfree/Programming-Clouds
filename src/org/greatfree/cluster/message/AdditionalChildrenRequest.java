package org.greatfree.cluster.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a more complicated design for scalability compared with HeavyWorkloadNotification/SuperfluousResourcesNotification. In the case, the children to join the task cluster are visible to programmers such that they can be configured or initialized before becoming a member of the task cluster. 09/12/2020, Bing Li
 */

// Created: 09/12/2020, Bing Li
public class AdditionalChildrenRequest extends ServerMessage
{
	private static final long serialVersionUID = -5804294475713882503L;
	
	private int size;

	public AdditionalChildrenRequest(int size)
	{
		super(ClusterMessageType.ADDITIONAL_CHILDREN_REQUEST);
		this.size = size;
	}

	public int getSize()
	{
		return this.size;
	}
}
