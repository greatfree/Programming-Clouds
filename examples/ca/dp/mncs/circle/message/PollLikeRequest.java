package ca.dp.mncs.circle.message;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PollLikeRequest extends ServerMessage
{
	private static final long serialVersionUID = 3814476323970878525L;
	
	private String postID;
	
	public PollLikeRequest(String postID)
	{
		super(CircleConfig.POLL_LIKE_REQUEST);
		this.postID = postID;
	}

	public String getPostID()
	{
		return this.postID;
	}
}
