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
public class InterChildrenHelloBroadNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 407286469823950458L;

	private String additionalMessage;

	public InterChildrenHelloBroadNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
