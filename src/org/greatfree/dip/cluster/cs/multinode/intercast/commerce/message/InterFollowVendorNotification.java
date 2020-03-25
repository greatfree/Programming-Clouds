package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.dip.cs.multinode.server.CSAccount;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/21/2019, Bing Li
public class InterFollowVendorNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = -5828740685426264037L;
	
	private CSAccount account;

	public InterFollowVendorNotification(IntercastNotification in, CSAccount account)
	{
		super(in);
		this.account = account;
	}

	public CSAccount getAccount()
	{
		return this.account;
	}
}
