package ca.dp.mncs.circle.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PollPostResponse extends ServerMessage
{
	private static final long serialVersionUID = 5951502109742034074L;
	
	private List<String> posts;

	public PollPostResponse(List<String> posts)
	{
		super(CircleConfig.POLL_POST_RESPONSE);
		this.posts = posts;
	}

	public List<String> getPosts()
	{
		return this.posts;
	}
}
