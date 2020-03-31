package ca.dp.mncs.circle.message;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PostRequest extends ServerMessage
{
	private static final long serialVersionUID = -6672459011222263909L;
	
	private String post;

	public PostRequest(String post)
	{
		super(CircleConfig.POST_REQUEST);
		this.post = post;
	}

	public String getPost()
	{
		return this.post;
	}
}
