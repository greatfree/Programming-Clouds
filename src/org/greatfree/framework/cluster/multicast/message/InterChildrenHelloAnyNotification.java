package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.IntercastNotification;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class InterChildrenHelloAnyNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 1500221462323130055L;

	private String additionalMessage;

	public InterChildrenHelloAnyNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
