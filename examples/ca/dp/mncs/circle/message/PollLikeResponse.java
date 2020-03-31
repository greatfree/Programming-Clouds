package ca.dp.mncs.circle.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class PollLikeResponse extends ServerMessage
{
	private static final long serialVersionUID = 7883685992231476926L;
	
	private List<String> friends;

	public PollLikeResponse(List<String> friends)
	{
		super(CircleConfig.POLL_LIKE_RESPONSE);
		this.friends = friends;
	}

	public List<String> getFriends()
	{
		return this.friends;
	}
}
