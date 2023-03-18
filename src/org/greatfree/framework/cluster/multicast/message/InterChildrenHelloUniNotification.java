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
public class InterChildrenHelloUniNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = -5980441548318629620L;
	
	private String additionalMessage;

	public InterChildrenHelloUniNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
