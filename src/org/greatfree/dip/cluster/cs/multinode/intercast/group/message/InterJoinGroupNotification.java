package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.dip.cs.multinode.server.CSAccount;
import org.greatfree.message.multicast.container.InterChildrenNotification;

// Created: 04/19/2019, In the Aircraft from Zhuhai to Xi'An, Immediately After Coming Back from Canada, Bing Li
public class InterJoinGroupNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 2233811874045554481L;
	
	private CSAccount account;

	public InterJoinGroupNotification(JoinGroupNotification in, CSAccount account)
	{
		super(in);
		this.account = account;
	}

	public CSAccount getAccount()
	{
		return this.account;
	}
}