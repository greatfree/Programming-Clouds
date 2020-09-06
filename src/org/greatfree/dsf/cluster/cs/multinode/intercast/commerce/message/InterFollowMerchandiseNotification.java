package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.dsf.cs.multinode.server.CSAccount;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/22/2019, Bing Li
public class InterFollowMerchandiseNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 7700359377368406345L;
	
	private CSAccount account;

	public InterFollowMerchandiseNotification(IntercastNotification in, CSAccount account)
	{
		super(in);
		this.account = account;
	}

	public CSAccount getAccount()
	{
		return this.account;
	}
}
