package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastRequest;

/**
 * 
 * @author libing
 * 
 * 06/21/2022
 *
 */
public class InterChildrenHelloBroadRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = -3583784804885619820L;

	private String additionalMessage;

	public InterChildrenHelloBroadRequest(IntercastRequest ir, String additionalMessage)
	{
		super(ir);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
