package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Notification;

// Created: 10/03/2019, Bing Li
public class SelfNotification extends Notification
{
	private static final long serialVersionUID = -310881468459967344L;
	
	private String msg;

	public SelfNotification(String msg)
	{
		super(P2PChatApplicationID.SELF_NOTIFICATION);
		this.msg = msg;
	}

	public String getMessage()
	{
		return this.msg;
	}
}
