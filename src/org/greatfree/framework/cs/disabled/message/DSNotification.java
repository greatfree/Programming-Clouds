package org.greatfree.framework.cs.disabled.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/07/2023
 *
 */
public class DSNotification extends Notification
{
	private static final long serialVersionUID = 7324924423539818249L;

	private String message;

	public DSNotification(String message)
	{
		super(DisabledAppID.DS_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
