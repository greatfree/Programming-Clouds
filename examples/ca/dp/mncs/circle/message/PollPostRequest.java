package ca.dp.mncs.circle.message;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PollPostRequest extends ServerMessage
{
	private static final long serialVersionUID = 8039383629263722570L;
	
	public PollPostRequest()
	{
		super(CircleConfig.POLL_POST_REQUEST);
	}

}
