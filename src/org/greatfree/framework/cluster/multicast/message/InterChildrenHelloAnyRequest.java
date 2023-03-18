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
public class InterChildrenHelloAnyRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = -497208808542465616L;

	private String additionalMessage;

	public InterChildrenHelloAnyRequest(IntercastRequest ir, String additionalMessage)
	{
		super(ir);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
