package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Notification;

// Created: 01/16/2019, Bing Li
public class LeaveClusterNotification extends Notification
{
	private static final long serialVersionUID = -8766681099467228184L;
	
	private String rootKey;
	private String childKey;

	public LeaveClusterNotification(String rootKey, String childKey)
	{
		super(P2PChatApplicationID.LEAVE_CLUSTER_NOTIFICATION);
		this.rootKey = rootKey;
		this.childKey = childKey;
	}

	public String getRootKey()
	{
		return this.rootKey;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
}
