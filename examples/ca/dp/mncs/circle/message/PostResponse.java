package ca.dp.mncs.circle.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PostResponse extends ServerMessage
{
	private static final long serialVersionUID = 4177117768564518896L;
	
	private List<String> posts;

	public PostResponse(List<String> posts)
	{
		super(CircleConfig.POST_RESPONSE);
		this.posts = posts;
	}

	public List<String> getPosts()
	{
		return this.posts;
	}
}
